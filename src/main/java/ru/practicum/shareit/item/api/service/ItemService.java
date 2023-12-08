package ru.practicum.shareit.item.api.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.api.dto.CommentDto;
import ru.practicum.shareit.item.api.dto.CommentSimpleDto;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    ItemDto create(Integer userId, ItemSimpleDto itemDto);

    ItemDto update(Integer userId, Integer itemId, ItemSimpleDto dto);

    ItemDto get(Integer userId, Integer itemId);

    List<ItemDto> getAll(Integer userId, Pageable pageable, LocalDateTime now);

    void delete(Integer userId, Integer itemId);

    List<ItemSimpleDto> search(String searchText, Pageable pageable);

    CommentDto createComment(CommentSimpleDto commentSimpleDto);
}
