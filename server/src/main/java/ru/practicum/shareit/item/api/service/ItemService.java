package ru.practicum.shareit.item.api.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.api.dto.CommentDto;
import ru.practicum.shareit.item.api.dto.CommentSimpleDto;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemSimpleDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemSimpleDto dto);

    ItemDto get(Long userId, Long itemId);

    List<ItemDto> getAll(Long userId, Pageable pageable, LocalDateTime now);

    List<ItemSimpleDto> search(String searchText, Pageable pageable);

    CommentDto createComment(CommentSimpleDto commentSimpleDto);
}
