package ru.practicum.shareit.user.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.entity.User;

@Mapper()
public interface UserMapper {
    UserMapper INSTANTS = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserSimpleDto userDto);

    User toEntityFromDto(UserDto userDto);
}
