package ru.practicum.shareit.item.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentSimpleDto {
    @NotBlank(groups = {Create.class})
    @Size(max = 2048, groups = {Create.class})
    private String text;
    private Long itemId;
    private Long authorId;
    private LocalDateTime created;
}
