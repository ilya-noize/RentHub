package ru.practicum.shareit.item.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.entity.CommentEntity;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "authorName", source = "entity.author.name")
    CommentDto toDto(CommentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item.id", source = "dto.itemId")
    @Mapping(target = "author.id", source = "dto.authorId")
    CommentEntity toEntity(CommentSimpleDto dto);
}