package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Just a class with data. Don't touch him.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestSimpleDto {
    private String description;
}
