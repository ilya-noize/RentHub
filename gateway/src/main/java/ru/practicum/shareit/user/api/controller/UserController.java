package ru.practicum.shareit.user.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.api.client.UserClient;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Positive;

import static ru.practicum.shareit.constants.Constants.CREATE_USER;
import static ru.practicum.shareit.constants.Constants.DELETE_USER;
import static ru.practicum.shareit.constants.Constants.GET_ALL_USERS;
import static ru.practicum.shareit.constants.Constants.GET_USER;
import static ru.practicum.shareit.constants.Constants.UPDATE_USER;

/**
 * <h3>User Controller</h3>
 * {@link #CREATE_USER} Создать пользователя <br/>
 * {@link #UPDATE_USER} Изменить пользователя <br/>
 * {@link #GET_USER}   Посмотреть пользователя <br/>
 * {@link #GET_ALL_USERS} Посмотреть всех пользователей <br/>
 * {@link #DELETE_USER} Удалить пользователя <br/>
 */

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
//    private final String CREATE_USER = "/users";
//    private final String UPDATE_USER = "/users/{id}";
//    private final String GET_USER = "/users/{id}";
//    private final String GET_ALL_USERS = "/users";
//    private final String DELETE_USER = "/users/{id}";

    private final UserClient userClient;


    @PostMapping(CREATE_USER)
    public ResponseEntity<Object> create(
            @RequestBody @Validated(Create.class) UserSimpleDto userDto) {

        return userClient.create(userDto);
    }

    @PatchMapping(UPDATE_USER)
    public ResponseEntity<Object> update(
            @PathVariable @Positive Integer id,
            @RequestBody @Validated(Update.class) UserDto userDto) {
        return userClient.update(id, userDto);
    }

    @GetMapping(GET_USER)
    @Validated
    public ResponseEntity<Object> get(
            @PathVariable @Positive Integer id) {

        return userClient.get(id);
    }

    @GetMapping(GET_ALL_USERS)
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @DeleteMapping(DELETE_USER)
    @Validated
    public void delete(
            @PathVariable @Positive Integer id) {

        userClient.delete(id);
    }
}