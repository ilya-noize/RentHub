package ru.practicum.shareit.user.api;

import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;

import java.util.List;

public interface UserService {
    UserDto create(UserSimpleDto userDto);

    UserDto get(Integer id);

    List<UserDto> getAll();

    UserDto update(UserDto userDto);

    void delete(Integer id);
}
