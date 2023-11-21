package ru.practicum.shareit.item.api.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.entity.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemSimpleDto toSimpleDto(Item item);

    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemDto toDto(Item item);

    @Mapping(target = "owner.id", source = "userId")
    Item toEntity(ItemDto itemDto, Integer userId);
}