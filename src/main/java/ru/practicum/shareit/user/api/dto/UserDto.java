package ru.practicum.shareit.user.api.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.*;

@Builder
@Data
public class UserDto {
    @PositiveOrZero
    private Integer id;

    @Email(groups = {Create.class, Update.class})
    @NotEmpty(groups = {Create.class})
    @Size(max = 255)
    private String email;

    @NotBlank(groups = {Create.class})
    @Size(max = 255)
    private String name;
}
