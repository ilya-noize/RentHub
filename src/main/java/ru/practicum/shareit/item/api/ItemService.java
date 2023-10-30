package ru.practicum.shareit.item.api;

import ru.practicum.shareit.item.api.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto get(Integer id);

    List<ItemDto> getAll(Integer userId);

    void delete(Integer userId, Integer itemId);

    List<ItemDto> search(String searchText);
}
