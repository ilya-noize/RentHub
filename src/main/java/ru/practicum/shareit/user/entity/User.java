package ru.practicum.shareit.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 * <p>
 * Class USER
 * <p>
 * {@code id} ID user <br/>
 * {@code name} User's name <br/>
 * {@code email} User's email <br/>
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
public class User {
    @Positive
    private final Integer id;
    @Email
    private final String email;
    private final String name;
}
