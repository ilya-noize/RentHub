package ru.practicum.shareit.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.api.CRUDController;
import ru.practicum.shareit.api.CRUDService;
import ru.practicum.shareit.user.api.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
public class UserController implements CRUDController<UserDto> {
    public static final String CREATE_USER = "/users";
    public static final String UPDATE_USER = "/users/{id}";
    public static final String GET_USER = "/users/{id}";
    public static final String DELETE_USER = "/users/{id}";
    public static final String GET_ALL_USERS = "/users";
    private final CRUDService<UserDto> service;

    @PostMapping(CREATE_USER)
    public UserDto create(
            @RequestBody @Valid UserDto userDto) {

        return service.create(userDto);
    }

    @PatchMapping(UPDATE_USER)
    @Validated
    public UserDto update(
            @PathVariable @Positive Integer id,
            @RequestBody @Valid UserDto userDto) {

        return service.update(id, userDto);
    }

    @GetMapping(GET_USER)
    @Validated
    public UserDto get(
            @PathVariable @Positive Integer id) {

        return service.get(id);
    }

    @GetMapping(GET_ALL_USERS)
    public List<UserDto> getAll() {

        return service.getAll();
    }

    @DeleteMapping(DELETE_USER)
    @Validated
    public void delete(
            @PathVariable @Positive Integer id) {

        service.delete(id);
    }
}


