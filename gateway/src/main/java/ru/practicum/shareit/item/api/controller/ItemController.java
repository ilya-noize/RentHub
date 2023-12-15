package ru.practicum.shareit.item.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.api.client.ItemClient;
import ru.practicum.shareit.item.api.dto.CommentSimpleDto;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final String FROM = "0";
    private final String SIZE = "10";
    private final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final String CREATE_ITEM = "/items";
    private final String UPDATE_ITEM = "/items/{id}";
    private final String GET_ITEM = "/items/{id}";
    private final String SEARCH_ITEM = "/items/search";
    private final String GET_ALL_ITEMS = "/items";
    private final String CREATE_COMMENT = "/items/{id}/comment";


    @PostMapping(CREATE_ITEM)
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @Validated(Create.class) @RequestBody ItemSimpleDto itemDto) {

        return itemClient.create(userId, itemDto);
    }

    @PatchMapping(UPDATE_ITEM)
    public ResponseEntity<Object> update(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @Validated(Update.class) @RequestBody ItemDto itemDto,
            @PathVariable long itemId) {

        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping(GET_ITEM)
    public ResponseEntity<Object> get(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable long itemId) {

        return itemClient.get(userId, itemId);
    }

    @GetMapping(SEARCH_ITEM)
    public ResponseEntity<Object> search(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {

        return itemClient.search(userId, text, from, size);
    }

    @GetMapping(GET_ALL_ITEMS)
    public ResponseEntity<Object> getAll(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {
        ResponseEntity<Object> items = itemClient.getAll(userId, from, size);

        return items;
    }

    @PostMapping(CREATE_COMMENT)
    public ResponseEntity<Object> addComment(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Integer itemId,
            @Valid @RequestBody CommentSimpleDto commentCreateDto) {

        return itemClient.createComment(userId, itemId, commentCreateDto);
    }
}