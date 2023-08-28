package net.proselyte.service.impl;

import net.proselyte.dao.CrudDao;
import net.proselyte.dto.EventDto;
import net.proselyte.dto.FileDto;
import net.proselyte.dto.UserDto;
import net.proselyte.dto.mapper.FileMapper;
import net.proselyte.entity.File;
import net.proselyte.exception.EntityNotFoundException;
import net.proselyte.service.EventService;
import net.proselyte.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {
    private static final List<String> TEST_FILENAMES = asList("/home/test_file_1", "/home/test_file_2");
    private static final String TEST_FILENAME = "/home/test_file";

    @Mock
    private FileMapper fileMapper;

    @Mock
    private CrudDao<File> fileDao;

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    void saveShouldSaveEntity() throws IOException {
        UserDto userDto = new UserDto();
        FileDto fileDto = new FileDto("test_file", "test data");
        File file = new File("test_file", "/home/test_file");
        when(userService.findById(any())).thenReturn(userDto);
        when(fileMapper.toFile(any())).thenReturn(file);
        when(fileMapper.toFileDto(any())).thenReturn(fileDto);
        when(fileDao.save(file)).thenReturn(file);

        assertThatCode(() -> fileService.save(fileDto, any())).doesNotThrowAnyException();

        verify(userService).findById(any());
        verify(eventService).save(new EventDto(userDto, fileDto));
        verify(fileMapper).toFile(any());
        verify(fileMapper).toFileDto(any());
        verify(fileDao).save(file);

        Files.delete(Paths.get(file.getFilePath()));
    }

    @Test
    void saveShouldThrowExceptionWhenHappensErrorWhileSavingFile() throws IOException {
        UserDto userDto = new UserDto();
        FileDto fileDto = new FileDto("test_file", "test data");
        File file = new File("test_file", "/home/");
        when(userService.findById(any())).thenReturn(userDto);
        when(fileMapper.toFile(any())).thenReturn(file);
        when(fileMapper.toFileDto(any())).thenReturn(fileDto);
        when(fileDao.save(file)).thenReturn(file);

        assertThatThrownBy(() -> fileService.save(fileDto, any())).isInstanceOf(UncheckedIOException.class);

        verify(userService).findById(any());
        verify(eventService, never()).save(new EventDto(userDto, fileMapper.toFileDto(file)));
        verify(fileMapper).toFile(any());
        verify(fileDao).save(file);
    }

    @Test
    void findByIdShouldReturnDto() throws IOException {
        createTestFile();

        FileDto fileDto = new FileDto("test_file", "data");
        File file = new File("test_file", "/home/test_file");
        when(fileDao.findById(any())).thenReturn(Optional.of(file));
        when(fileMapper.toFileDto(file)).thenReturn(fileDto);

        assertThat(fileService.findById(any())).isEqualTo(fileDto);

        verify(fileMapper).toFileDto(any());
        verify(fileDao).findById(any());

        deleteTestFile();
    }

    @Test
    void findByIdShouldThrowExceptionIfThereIsNoEntity() {
        when(fileDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.findById(any()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("File not found!");

        verify(fileDao).findById(any());
    }

    @Test
    void findByIdShouldThrowExceptionIfThereIsNoFile() {
        FileDto fileDto = new FileDto("test_file", "data");
        File file = new File("test_file", "/home/");
        when(fileDao.findById(any())).thenReturn(Optional.of(file));
        when(fileMapper.toFileDto(file)).thenReturn(fileDto);

        assertThatThrownBy(() -> fileService.findById(any()))
                .isInstanceOf(UncheckedIOException.class);

        verify(fileMapper).toFileDto(any());
        verify(fileDao).findById(any());
    }

    @Test
    void findAllShouldReturnListOfDto() {
        createTestFiles();

        List<FileDto> filesDto = asList(new FileDto("test_file_1", "data"),
                                        new FileDto("test_file_2", "data"));
        List<File> files = asList(new File("test_file_1", "/home/test_file_1"),
                                  new File("test_file_2", "/home/test_file_1"));

        when(fileDao.findAll()).thenReturn(files);
        when(fileMapper.toFileDto(any())).thenReturn(filesDto.get(0))
                                         .thenReturn(filesDto.get(1));

        assertThat(fileService.findAll()).isEqualTo(filesDto);

        verify(fileDao).findAll();
        verify(fileMapper, times(2)).toFileDto(any());

        deleteTestFiles();
    }


    @Test
    void updateShouldUpdateEntity() throws IOException {
        createTestFile();

        FileDto fileDto = new FileDto("test_file", "data");
        File file = new File("test_file", "/home/test_file");
        when(fileDao.findById(any())).thenReturn(Optional.of(file));

        assertThatCode(() -> fileService.update(any(), fileDto)).doesNotThrowAnyException();

        verify(fileDao).findById(any());

        deleteTestFile();
    }

    @Test
    void updateShouldThrowExceptionIfThereIsNoEntity() {
        FileDto fileDto = new FileDto("test_file", "data");
        when(fileDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.update(any(), fileDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("File not found!");

        verify(fileDao).findById(any());
    }

    @Test
    void removeShouldDeleteEntity() throws IOException {
        createTestFile();

        File file = new File("test_file", "/home/test_file");
        when(fileDao.findById(any())).thenReturn(Optional.of(file));
        fileService.remove(any());

        verify(fileDao).findById(any());
        verify(fileDao).deleteById(any());
    }

    @Test
    void removeShouldThrowExceptionIfThereIsNoEntity() throws IOException {
        when(fileDao.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fileService.remove(any()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("File not found!");

        verify(fileDao).findById(any());
        verify(fileDao, never()).deleteById(any());
    }

    @Test
    void removeShouldThrowExceptionIfThereIsNoFile() throws IOException {
        File file = new File("test_file", "/home/");
        when(fileDao.findById(any())).thenReturn(Optional.of(file));
        assertThatThrownBy(() -> fileService.remove(any()))
                .isInstanceOf(UncheckedIOException.class);

        verify(fileDao).findById(any());
        verify(fileDao, never()).deleteById(any());
    }

    private static void deleteTestFiles() {
        TEST_FILENAMES.forEach(filename -> {
            Path path = Paths.get(filename);
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void createTestFiles() {
        TEST_FILENAMES.forEach(filename -> {
            Path path = Paths.get(filename);
            if (!Files.exists(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void deleteTestFile() throws IOException {
        Path path = Paths.get(TEST_FILENAME);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    private static void createTestFile() throws IOException {
        Path path = Paths.get(TEST_FILENAME);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

}
