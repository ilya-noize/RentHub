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
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.api.service.UserService;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping(Constants.CREATE_USER)
    public UserDto create(
            @RequestBody @Validated(Create.class) UserSimpleDto userDto) {

        return service.create(userDto);
    }

    @PatchMapping(Constants.UPDATE_USER)
    public UserDto update(
            @PathVariable @Positive Integer id,
            @RequestBody @Validated(Update.class) UserDto userDto) {
        userDto.setId(id);
        return service.update(userDto);
    }

    @GetMapping(Constants.GET_USER)
    @Validated
    public UserDto get(
            @PathVariable @Positive Integer id) {

        return service.get(id);
    }

    @GetMapping(Constants.GET_ALL_USERS)
    public List<UserDto> getAll() {

        return service.getAll();
    }

    @DeleteMapping(Constants.DELETE_USER)
    @Validated
    public void delete(
            @PathVariable @Positive Integer id) {

        service.delete(id);
    }
}