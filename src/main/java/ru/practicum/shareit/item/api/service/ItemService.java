package ru.practicum.shareit.item.api.service;

import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.comment.api.dto.CommentDtoRecord;
import ru.practicum.shareit.item.comment.api.dto.CommentSimpleDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Integer userId, ItemSimpleDto itemDto);

    ItemDto update(Integer userId, Integer itemId, ItemSimpleDto dto);

    ItemDto get(Integer userId, Integer itemId);

    List<ItemDto> getAll(Integer userId);

    void delete(Integer userId, Integer itemId);

    List<ItemSimpleDto> search(String searchText);

    CommentDtoRecord createComment(CommentSimpleDto commentSimpleDto);
}
