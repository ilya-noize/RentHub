package ru.practicum.shareit.item.comment.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.entity.CommentEntity;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "authorName", source = "entity.author.name")
    CommentDtoRecord toDtoRecord(CommentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item.id", source = "dto.itemId")
    @Mapping(target = "author.id", source = "dto.authorId")
    CommentEntity toEntity(CommentSimpleDto dto);
}