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
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.valid.Checking;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@RestController
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping(Constants.CREATE_REQUEST)
    public ItemRequestDto create(
            @RequestHeader(Constants.HEADER_USER_ID) int requesterId,
            @RequestBody
            @Validated(Create.class) ItemRequestSimpleDto itemRequestSimpleDto) {

        return itemRequestService.create(requesterId, itemRequestSimpleDto, LocalDateTime.now());
    }

    @GetMapping(Constants.GET_BY_REQUESTER)
    public List<ItemRequestDto> getByRequesterId(
            @RequestHeader(Constants.HEADER_USER_ID) Integer requesterId) {

        return itemRequestService.getByRequesterId(requesterId);
    }

    @GetMapping(Constants.GET_ALL_REQUESTS)
    @Validated
    public List<ItemRequestDto> getAll(
            @RequestHeader(Constants.HEADER_USER_ID) Integer requesterId,
            @RequestParam(required = false, defaultValue = Constants.FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = Constants.SIZE)
            @Positive Integer size) {

        return itemRequestService.getAll(requesterId, Checking.checkPageable(from, size));
    }

    @GetMapping(Constants.GET_REQUEST)
    public ItemRequestDto get(
            @RequestHeader(Constants.HEADER_USER_ID) Integer requesterId,
            @PathVariable Integer id) {

        return itemRequestService.get(requesterId, id);
    }
}
