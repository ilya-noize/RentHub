package ru.practicum.shareit.item.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(uses = {CommentMapper.class, ItemRequestMapper.class},
        injectionStrategy = CONSTRUCTOR)
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemSimpleDto toSimpleDto(Item item);

    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "requestId", source = "entity.request.id")
    ItemDto toDto(Item entity);

    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner.id", source = "userId")
    Item toEntity(ItemSimpleDto itemDto, Long userId);
}