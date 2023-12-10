package ru.practicum.shareit.item.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.api.dto.CommentDto;
import ru.practicum.shareit.item.api.dto.CommentSimpleDto;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.service.ItemService;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.*;

@RestController
@RequiredArgsConstructor
public class ItemController {
    public static final String CREATE_ITEM = "/items";
    public static final String UPDATE_ITEM = "/items/{id}";
    public static final String GET_ITEM = "/items/{id}";
    public static final String SEARCH_ITEM = "/items/search";
    public static final String GET_ALL_ITEMS = "/items";
    public static final String CREATE_COMMENT = "/items/{id}/comment";
    private final ItemService service;

    @PostMapping(CREATE_ITEM)
    public ItemDto create(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @RequestBody
            @Validated(Create.class) ItemSimpleDto itemDto) {

        return service.create(userId, itemDto);
    }

    @PatchMapping(UPDATE_ITEM)
    public ItemDto update(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable(name = "id") Integer itemId,
            @RequestBody
            @Validated(Update.class) ItemSimpleDto itemDto) {

        return service.update(userId, itemId, itemDto);
    }

    @GetMapping(GET_ITEM)
    public ItemDto get(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable(name = "id") Integer itemId) {

        return service.get(userId, itemId);
    }

    @GetMapping(SEARCH_ITEM)
    public List<ItemSimpleDto> search(
            @RequestParam(name = "text") String textSearch,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {

        return service.search(textSearch, PageRequest.of(from / size, size));
    }

    @GetMapping(GET_ALL_ITEMS)
    public List<ItemDto> getAll(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {

        return service.getAll(userId, PageRequest.of(from / size, size), LocalDateTime.now());
    }

    @PostMapping(CREATE_COMMENT)
    public CommentDto createComment(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable(name = "id") Integer itemId,
            @RequestBody
            @Validated(Create.class) CommentSimpleDto commentSimpleDto) {
        commentSimpleDto.setItemId(itemId);
        commentSimpleDto.setAuthorId(userId);
        commentSimpleDto.setCreated(LocalDateTime.now());

        return service.createComment(commentSimpleDto);
    }
}
