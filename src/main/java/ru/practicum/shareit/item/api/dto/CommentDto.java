package ru.practicum.shareit.item.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommentDto {
    private final Integer id;
    private final String text;
    private final Integer itemId;
    private final Integer authorId;
}
