package ru.practicum.shareit.user.api.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

@Builder
@Data
public class UserDto {
    @PositiveOrZero
    private Integer id;
    @Email(groups = {Create.class, Update.class})
    @NotEmpty(groups = {Create.class})
    private String email;
    @NotBlank(groups = {Create.class})
    private String name;
}
