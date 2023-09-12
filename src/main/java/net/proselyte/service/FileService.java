package net.proselyte.service;

import net.proselyte.dto.FileDto;

import java.util.List;

public interface FileService {
    void save(FileDto fileDto, Integer userId);

    FileDto findById(Integer id);

    List<FileDto> findAll();

    void update(Integer id, FileDto fileDto);

    void remove(Integer id);
}
