package ru.practicum.shareit.item.comment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CommentSimpleDto {
    @NotBlank(groups = {Create.class})
    @Size(max = 2048, groups = {Create.class})
    private String text;
    private Integer itemId;
    private Integer authorId;
    private LocalDateTime created;
}
