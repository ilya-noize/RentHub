package ru.practicum.shareit.user.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserDto {
    @PositiveOrZero
    private Integer id;

    @Email(groups = {Update.class})
    @Size(max = 255, groups = {Update.class})
    private String email;

    @Size(max = 255, groups = {Update.class})
    private String name;
}
