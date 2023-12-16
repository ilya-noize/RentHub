package ru.practicum.shareit.item.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentSimpleDto {
    private String text;
    private Long itemId;
    private Long authorId;
    private LocalDateTime created;
}
