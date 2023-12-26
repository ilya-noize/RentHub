package ru.practicum.shareit.request.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestSimpleDto itemRequestSimpleDto, LocalDateTime now);

    ItemRequestDto get(Long userId, Long itemRequestId);

    List<ItemRequestDto> getAll(Long userId, Pageable pageable);

    List<ItemRequestDto> getByRequesterId(Long requester);
}