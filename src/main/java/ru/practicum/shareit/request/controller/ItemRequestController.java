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

import static ru.practicum.shareit.ShareItApp.HEADER_USER_ID;
import static ru.practicum.shareit.ShareItApp.checkPageable;

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
            @RequestHeader(HEADER_USER_ID)
            int userId,
            //todo [!] Received the status 400 BAD_REQUEST Error: null
            @RequestBody @Validated(Create.class)
            ItemRequestSimpleDto itemRequestSimpleDto) {

        return itemRequestService.create(userId, itemRequestSimpleDto, LocalDateTime.now());
    }

    @GetMapping(GET_BY_REQUESTER)
    public List<ItemRequestDto> getByRequesterId(
            @RequestHeader(HEADER_USER_ID)
            Integer requesterId) {

        return itemRequestService.getByRequesterId(requesterId);
    }

    @GetMapping(GET_ALL_REQUESTS)
    @Validated
    public List<ItemRequestDto> getAll(
            @RequestHeader(HEADER_USER_ID)
            Integer userId,
            @RequestParam(required = false, defaultValue = "0")
            @PositiveOrZero
            Integer from,
            @RequestParam(required = false, defaultValue = "50")
            @Positive
            Integer size) {
//        Pageable pageable = PageRequest.of(from / size, size);

        return itemRequestService.getAll(userId, checkPageable(from, size));
    }

    @GetMapping(GET_REQUEST)
    public ItemRequestDto get(
            @RequestHeader(HEADER_USER_ID)
            Integer userId,
            @PathVariable
            Integer id) {

        return itemRequestService.get(userId, id);
    }
}
