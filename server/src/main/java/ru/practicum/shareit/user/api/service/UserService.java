package ru.practicum.shareit.user.api.service;

import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;

import java.util.List;

public interface UserService {
    UserDto create(UserSimpleDto userDto);

    UserDto get(Long id);

    List<UserDto> getAll();

    UserDto update(UserDto userDto);

    void delete(Long id);
}
