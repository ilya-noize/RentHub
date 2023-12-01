package ru.practicum.shareit.item.comment.api.dto;

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
public class CommentDtoRecord {
    private final Integer id;
    private final String text;
    private final String authorName;
    private LocalDateTime created;
}
