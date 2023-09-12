package net.proselyte.dto.mapper;

import net.proselyte.dto.FileDto;
import net.proselyte.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    @Mapping(target = "data", ignore = true)
    FileDto toFileDto(File file);

    File toFile(FileDto fileDto);
}
