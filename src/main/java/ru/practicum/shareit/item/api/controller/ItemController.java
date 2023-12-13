package ru.practicum.shareit.item.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.api.dto.CommentDto;
import ru.practicum.shareit.item.api.dto.CommentSimpleDto;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.service.ItemService;
import ru.practicum.shareit.valid.Checking;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping(Constants.CREATE_ITEM)
    public ItemDto create(
            @RequestHeader(Constants.HEADER_USER_ID) Integer userId,
            @RequestBody
            @Validated(Create.class) ItemSimpleDto itemDto) {

        return service.create(userId, itemDto);
    }

    @PatchMapping(Constants.UPDATE_ITEM)
    public ItemDto update(
            @RequestHeader(Constants.HEADER_USER_ID) Integer userId,
            @PathVariable(name = "id") Integer itemId,
            @RequestBody
            @Validated(Update.class) ItemSimpleDto itemDto) {

        return service.update(userId, itemId, itemDto);
    }

    @GetMapping(Constants.GET_ITEM)
    public ItemDto get(
            @RequestHeader(Constants.HEADER_USER_ID) Integer userId,
            @PathVariable(name = "id") Integer itemId) {

        return service.get(userId, itemId);
    }

    @GetMapping(Constants.SEARCH_ITEM)
    public List<ItemSimpleDto> search(
            @RequestParam(name = "text") String textSearch,
            @RequestParam(required = false, defaultValue = Constants.FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = Constants.SIZE)
            @Positive Integer size) {

        return service.search(textSearch, Checking.checkPageable(from, size));
    }

    @GetMapping(Constants.GET_ALL_ITEMS)
    public List<ItemDto> getAll(
            @RequestHeader(Constants.HEADER_USER_ID) Integer userId,
            @RequestParam(required = false, defaultValue = Constants.FROM)
            @PositiveOrZero Integer from,

            @RequestParam(required = false, defaultValue = Constants.SIZE)
            @Positive Integer size) {

        return service.getAll(userId, Checking.checkPageable(from, size), LocalDateTime.now());
    }

    @PostMapping(Constants.CREATE_COMMENT)
    public CommentDto createComment(
            @RequestHeader(Constants.HEADER_USER_ID) Integer userId,
            @PathVariable(name = "id") Integer itemId,
            @RequestBody
            @Validated(Create.class) CommentSimpleDto commentSimpleDto) {
        commentSimpleDto.setItemId(itemId);
        commentSimpleDto.setAuthorId(userId);
        commentSimpleDto.setCreated(LocalDateTime.now());

        return service.createComment(commentSimpleDto);
    }
}
