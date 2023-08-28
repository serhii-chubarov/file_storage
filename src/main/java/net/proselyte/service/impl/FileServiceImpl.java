package net.proselyte.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.dao.CrudDao;
import net.proselyte.dto.EventDto;
import net.proselyte.dto.FileDto;
import net.proselyte.dto.UserDto;
import net.proselyte.dto.mapper.FileMapper;
import net.proselyte.entity.File;
import net.proselyte.exception.EntityNotFoundException;
import net.proselyte.service.EventService;
import net.proselyte.service.FileService;
import net.proselyte.service.UserService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    private static final String INIT_PATH = "/home/";
    private static final String FILE_NOT_FOUND = "File not found!";

    private static final Function<FileMapper, Function<File, FileDto>> FILE_TO_FILE_DTO =
            mapper -> file -> {
                FileDto fileDto = mapper.toFileDto(file);
                String data = readFile(fileDto.getName());
                fileDto.setData(data);
                return fileDto;
            };

    private final FileMapper fileMapper;
    private final CrudDao<File> fileDao;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public void save(FileDto fileDto, Integer userId) {
        log.debug("Service for saving file {}", fileDto);
        UserDto userDto = userService.findById(userId);
        fileDto.setFilePath(INIT_PATH + fileDto.getName());
        File file = fileDao.save(fileMapper.toFile(fileDto));
        saveFile(file.getFilePath(), fileDto.getData());
        eventService.save(new EventDto(userDto, fileMapper.toFileDto(file)));
    }

    @Override
    public FileDto findById(Integer id) {
        log.debug("Service for finding file by id");
        return fileDao.findById(id)
                      .map(FILE_TO_FILE_DTO.apply(fileMapper))
                      .orElseThrow(() -> new EntityNotFoundException(FILE_NOT_FOUND));
    }

    @Override
    public List<FileDto> findAll() {
        log.debug("Service for finding all files");
        return fileDao.findAll()
                      .stream()
                      .map(FILE_TO_FILE_DTO.apply(fileMapper))
                      .toList();
    }

    @Override
    public void update(Integer id, FileDto fileDto) {
        log.debug("Service for updating file");
        File file = fileDao.findById(id)
                           .orElseThrow(() -> new EntityNotFoundException(FILE_NOT_FOUND));
        saveFile(file.getFilePath(), fileDto.getData());
    }

    @Override
    public void remove(Integer id) {
        log.debug("Service for removing file");
        File file = fileDao.findById(id)
                           .orElseThrow(() -> new EntityNotFoundException(FILE_NOT_FOUND));
        deleteFile(file);
        fileDao.deleteById(id);
    }

    private static void saveFile(String filename, String data) {
        log.debug("Save file {}", filename);
        try (Writer writer = new FileWriter(filename)) {
            writer.write(data);
        } catch (IOException exception) {
            log.debug("Fail while saving file {}", exception.getMessage());
            throw new UncheckedIOException(exception);
        }
    }

    private static String readFile(String filename) {
        log.debug("Read file {}", filename);
        String data;
        try (Stream<String> lines = Files.lines(Paths.get(INIT_PATH + filename))) {
            data = lines.collect(Collectors.joining());
        } catch (IOException exception) {
            log.debug("Fail while reading file {}", exception.getMessage());
            throw new UncheckedIOException(exception);
        }

        return data;
    }

    private static void deleteFile(File file) {
        log.debug("Delete file {}", file);
        try {
            Files.delete(Paths.get(file.getFilePath()));
        } catch (IOException e) {
            log.debug("Fail while deleting file {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }
}
