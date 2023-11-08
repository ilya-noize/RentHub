package ru.practicum.shareit.item.api.dto;

import org.mapstruct.*;
import ru.practicum.shareit.item.entity.CommentEntity;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto toDto(CommentEntity commentEntity);
}