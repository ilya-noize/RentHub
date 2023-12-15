package ru.practicum.shareit.request.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Integer userId, ItemRequestSimpleDto itemRequestSimpleDto, LocalDateTime now);

    ItemRequestDto get(Integer userId, Integer itemRequestId);

    List<ItemRequestDto> getAll(Integer userId, Pageable pageable);

    List<ItemRequestDto> getByRequesterId(Integer requester);
}