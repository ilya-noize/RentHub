package ru.practicum.shareit.item.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.api.dto.CommentDto;
import ru.practicum.shareit.item.api.dto.CommentSimpleDto;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.api.controller.BookingController.checkPageable;
import static ru.practicum.shareit.constants.Constants.CREATE_COMMENT;
import static ru.practicum.shareit.constants.Constants.CREATE_ITEM;
import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.GET_ALL_ITEMS;
import static ru.practicum.shareit.constants.Constants.GET_ITEM;
import static ru.practicum.shareit.constants.Constants.HEADER_USER_ID;
import static ru.practicum.shareit.constants.Constants.SEARCH_ITEM;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.UPDATE_ITEM;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping(CREATE_ITEM)
    public ItemDto create(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestBody ItemSimpleDto itemDto) {

        return service.create(userId, itemDto);
    }

    @PatchMapping(UPDATE_ITEM)
    public ItemDto update(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable(name = "id") Long itemId,
            @RequestBody ItemSimpleDto itemDto) {

        return service.update(userId, itemId, itemDto);
    }

    @GetMapping(GET_ITEM)
    public ItemDto get(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable(name = "id") Long itemId) {

        return service.get(userId, itemId);
    }

    @GetMapping(SEARCH_ITEM)
    public List<ItemSimpleDto> search(
            @RequestParam(name = "text") String textSearch,
            @RequestParam(required = false, defaultValue = FROM)
            Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            Integer size) {

        return service.search(textSearch, checkPageable(from, size));
    }

    @GetMapping(GET_ALL_ITEMS)
    public List<ItemDto> getAll(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = FROM)
            Integer from,

            @RequestParam(required = false, defaultValue = SIZE)
            Integer size) {

        return service.getAll(userId, checkPageable(from, size), LocalDateTime.now());
    }

    @PostMapping(CREATE_COMMENT)
    public CommentDto createComment(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable(name = "id") Long itemId,
            @RequestBody CommentSimpleDto commentSimpleDto) {
        commentSimpleDto.setItemId(itemId);
        commentSimpleDto.setAuthorId(userId);
        commentSimpleDto.setCreated(LocalDateTime.now());

        return service.createComment(commentSimpleDto);
    }
}
