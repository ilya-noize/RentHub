package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.api.controller.BookingController.checkPageable;
import static ru.practicum.shareit.constants.Constants.CREATE_REQUEST;
import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.GET_ALL_REQUESTS;
import static ru.practicum.shareit.constants.Constants.GET_BY_REQUESTER;
import static ru.practicum.shareit.constants.Constants.GET_REQUEST;
import static ru.practicum.shareit.constants.Constants.HEADER_USER_ID;
import static ru.practicum.shareit.constants.Constants.SIZE;


/**
 *
 */
@RestController
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping(CREATE_REQUEST)
    public ItemRequestDto create(
            @RequestHeader(HEADER_USER_ID) int requesterId,
            @RequestBody
            ItemRequestSimpleDto itemRequestSimpleDto) {

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
            Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            Integer size) {

        return itemRequestService.getAll(requesterId, checkPageable(from, size));
    }

    @GetMapping(GET_REQUEST)
    public ItemRequestDto get(
            @RequestHeader(HEADER_USER_ID) Integer requesterId,
            @PathVariable Integer id) {

        return itemRequestService.get(requesterId, id);
    }
}
