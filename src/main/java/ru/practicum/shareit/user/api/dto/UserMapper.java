package ru.practicum.shareit.user.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserSimpleDto userDto);

    User toEntityFromDto(UserDto userDto);
}
