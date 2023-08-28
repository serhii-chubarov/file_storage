package net.proselyte.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDto {
    Integer id;
    String name;
    String filePath;
    String data;

    public FileDto(String name, String data) {
        this.name = name;
        this.data = data;
    }
}
