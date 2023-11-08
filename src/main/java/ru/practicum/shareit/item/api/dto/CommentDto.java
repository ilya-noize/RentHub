package ru.practicum.shareit.item.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
public class CommentDto {
    @PositiveOrZero
    private Integer id;
    @NotBlank
    private String commentText;
    @NotNull
    private Integer itemId;
    @NotNull
    private Integer authorId;
}
