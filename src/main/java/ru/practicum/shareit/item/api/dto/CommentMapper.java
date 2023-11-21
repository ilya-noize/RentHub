package ru.practicum.shareit.item.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.entity.CommentEntity;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "itemId", source = "entity.item.id")
    @Mapping(target = "authorId", source = "entity.author.id")
    CommentDto toDto(CommentEntity entity);
}