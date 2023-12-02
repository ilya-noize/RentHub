package ru.practicum.shareit.user.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserSimpleDto {
    @Email(groups = {Create.class})
    @NotEmpty(groups = {Create.class})
    @Size(max = 255, groups = {Create.class})
    private String email;

    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class})
    private String name;
}
