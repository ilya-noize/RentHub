package ru.practicum.shareit.user.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.api.service.UserService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.CREATE_USER;
import static ru.practicum.shareit.constants.Constants.DELETE_USER;
import static ru.practicum.shareit.constants.Constants.GET_ALL_USERS;
import static ru.practicum.shareit.constants.Constants.GET_USER;
import static ru.practicum.shareit.constants.Constants.UPDATE_USER;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping(CREATE_USER)
    public UserDto create(
            @RequestBody UserSimpleDto userDto) {

        return service.create(userDto);
    }

    @PatchMapping(UPDATE_USER)
    public UserDto update(
            @PathVariable Integer id,
            @RequestBody UserDto userDto) {
        userDto.setId(id);
        return service.update(userDto);
    }

    @GetMapping(GET_USER)
    @Validated
    public UserDto get(
            @PathVariable Integer id) {

        return service.get(id);
    }

    @GetMapping(GET_ALL_USERS)
    public List<UserDto> getAll() {

        return service.getAll();
    }

    @DeleteMapping(DELETE_USER)
    @Validated
    public void delete(
            @PathVariable Integer id) {

        service.delete(id);
    }
}