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
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;


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

        return itemClient.getAll(userId, from, size);
    }

    @PostMapping(CREATE_COMMENT)
    public ResponseEntity<Object> addComment(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Integer itemId,
            @Valid @RequestBody CommentSimpleDto commentCreateDto) {

        return itemClient.createComment(userId, itemId, commentCreateDto);
    }
}