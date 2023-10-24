package ru.practicum.shareit.user.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;

@Builder
@Data
public class UserDto {
    @Positive
    private Integer id;
    @Email
    private String email;
    private String name;
}
