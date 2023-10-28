package ru.practicum.shareit.user.api.dto;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.practicum.shareit.user.entity.User;

@Mapper(componentModel = "spring")
@Qualifier("UserMapper")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
