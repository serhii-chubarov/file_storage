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
public class EventDto {
    Integer id;
    UserDto user;
    FileDto file;

    public EventDto(UserDto user, FileDto file) {
        this.user = user;
        this.file = file;
    }
}
