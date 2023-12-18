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
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.api.client.UserClient;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Positive;

/**
 * <h3>User Controller</h3>
 * {@link #createUser} Создать пользователя <br/>
 * {@link #updateUser} Изменить пользователя <br/>
 * {@link #getUser}   Посмотреть пользователя <br/>
 * {@link #deleteUser} Удалить пользователя <br/>
 * {@link #getAllUsers} Посмотреть всех пользователей <br/>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final String createUser = "/users";
    private final String updateUser = "/users/{id}";
    private final String getUser = "/users/{id}";
    private final String deleteUser = "/users/{id}";
    private final String getAllUsers = "/users";
    private final UserClient userClient;


    @PostMapping(createUser)
    public ResponseEntity<Object> create(
            @RequestBody @Validated(Create.class) UserSimpleDto userDto) {
        log.info("[i] create user {}", userDto);

        return userClient.create(userDto);
    }

    @PatchMapping(updateUser)
    public ResponseEntity<Object> update(
            @PathVariable @Positive Long id,
            @RequestBody @Validated(Update.class) UserDto userDto) {
        log.info("[i] update user {}", userDto);

        return userClient.update(id, userDto);
    }

    @GetMapping(getUser)
    @Validated
    public ResponseEntity<Object> getUsersById(
            @PathVariable Long id) {
        log.info("[i] get user {}", id);

        return userClient.getById(id);
    }

    @GetMapping(getAllUsers)
    public ResponseEntity<Object> getAll() {
        log.info("[i] get users");

        return userClient.getAll();
    }

    @DeleteMapping(deleteUser)
    @Validated
    public void deleteUser(
            @PathVariable Long id) {
        log.info("[i] delete user {}", id);
        userClient.delete(id);
    }

}