package net.proselyte.dto.mapper;

import net.proselyte.dto.UserDto;
import net.proselyte.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);

    @Mapping(target = "events", ignore = true)
    User toUser(UserDto userDto);
}
