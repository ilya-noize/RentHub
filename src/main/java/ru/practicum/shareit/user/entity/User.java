package ru.practicum.shareit.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Class USER
 * <p>
 * {@code id} ID user <br/>
 * {@code name} User's name <br/>
 * {@code email} User's email <br/>
 */
@Builder
@Getter
@AllArgsConstructor
public class User {
    private final Integer id;
    private final String email;
    private final String name;
}
