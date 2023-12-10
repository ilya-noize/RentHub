package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Just a class with data. Don't touch him.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestSimpleDto {
    @NotBlank(groups = {Create.class})
    @Size(max = 2000, groups = {Create.class})
    private String description;
}
