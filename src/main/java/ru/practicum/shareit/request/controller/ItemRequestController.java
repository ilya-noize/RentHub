package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.*;

/**
 *
 */
@RestController
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String CREATE_REQUEST = "/requests";
    public static final String GET_BY_REQUESTER = "/requests";
    public static final String GET_REQUEST = "/requests/{id}";
    public static final String GET_ALL_REQUESTS = "/requests/all";
    private final ItemRequestService itemRequestService;

    @PostMapping(CREATE_REQUEST)
    public ItemRequestDto create(
            @RequestHeader(HEADER_USER_ID) int requesterId,
            @RequestBody
            @Validated(Create.class) ItemRequestSimpleDto itemRequestSimpleDto) {

        return itemRequestService.create(requesterId, itemRequestSimpleDto, LocalDateTime.now());
    }

    @GetMapping(GET_BY_REQUESTER)
    public List<ItemRequestDto> getByRequesterId(
            @RequestHeader(HEADER_USER_ID) Integer requesterId) {

        return itemRequestService.getByRequesterId(requesterId);
    }

    @GetMapping(GET_ALL_REQUESTS)
    @Validated
    public List<ItemRequestDto> getAll(
            @RequestHeader(HEADER_USER_ID) Integer requesterId,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {

        return itemRequestService.getAll(requesterId, checkPageable(from, size));
    }

    @GetMapping(GET_REQUEST)
    public ItemRequestDto get(
            @RequestHeader(HEADER_USER_ID) Integer requesterId,
            @PathVariable Integer id) {

        return itemRequestService.get(requesterId, id);
    }
}
