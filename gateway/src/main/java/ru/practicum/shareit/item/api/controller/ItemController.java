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

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.HEADER_USER_ID;
import static ru.practicum.shareit.constants.Constants.SIZE;

@RestController
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping("/items")
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Validated(Create.class) @RequestBody ItemSimpleDto itemDto) {

        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Validated(Update.class) @RequestBody ItemDto itemDto,
            @PathVariable long id) {

        return itemClient.update(userId, itemDto, id);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Object> get(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable long id) {

        return itemClient.get(userId, id);
    }

    @GetMapping("/items/search")
    public ResponseEntity<Object> search(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {

        return itemClient.search(userId, text, from, size);
    }

    @GetMapping("/items")
    public ResponseEntity<Object> getAll(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {

        return itemClient.getAll(userId, from, size);
    }

    @PostMapping("/items/{id}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody CommentSimpleDto commentCreateDto) {

        return itemClient.createComment(userId, id, commentCreateDto);
    }
}