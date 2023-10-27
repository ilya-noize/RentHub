package ru.practicum.shareit.item.api.dto;

import ru.practicum.shareit.api.Mapper;
import ru.practicum.shareit.item.entity.Item;

public interface ItemMapper extends Mapper<Item, ItemDto> {
    Item toEntity(ItemDto itemDto, Integer userId);
}
