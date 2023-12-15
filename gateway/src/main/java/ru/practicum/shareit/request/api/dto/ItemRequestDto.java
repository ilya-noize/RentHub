package ru.practicum.shareit.request.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.api.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Just a class with data. Don't touch him.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
