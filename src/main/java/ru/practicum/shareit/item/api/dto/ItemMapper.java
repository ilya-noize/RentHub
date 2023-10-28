package ru.practicum.shareit.item.api.dto;

import ru.practicum.shareit.item.entity.Item;

public interface ItemMapper {
    ItemDto toDto(Item item);
    Item toEntity(ItemDto itemDto, Integer userId);
}
