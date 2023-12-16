package ru.practicum.shareit.item.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Just a class with data. Don't touch him.
 */

@AllArgsConstructor
@Getter
@Setter
public class CommentDto {
    private final Long id;
    private final String text;
    private final String authorName;
    private LocalDateTime created;
}
