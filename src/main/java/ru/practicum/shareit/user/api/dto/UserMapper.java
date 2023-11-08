package ru.practicum.shareit.user.api.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
