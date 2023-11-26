package ru.practicum.shareit.item.comment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CommentDtoSource {
    private final Integer id;
    @NotBlank(groups = {Create.class})
    private final String text;
    private final Integer itemId;
    private final Integer authorId;
    private LocalDateTime created;
}
