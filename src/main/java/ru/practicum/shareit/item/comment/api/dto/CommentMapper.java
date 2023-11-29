package ru.practicum.shareit.item.comment.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.entity.CommentEntity;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "authorName", source = "entity.author.name")
    CommentDtoRecord toDtoRecord(CommentEntity entity);
}