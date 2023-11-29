package ru.practicum.shareit.item.api;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.comment.api.dto.CommentDtoRecord;
import ru.practicum.shareit.item.comment.api.dto.CommentDtoSource;
import ru.practicum.shareit.valid.group.Create;

import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.HEADER_USER_ID;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    public static final String CREATE_ITEM = "/items";
    public static final String UPDATE_ITEM = "/items/{id}";
    public static final String GET_ITEM = "/items/{id}";
    public static final String SEARCH_ITEM = "/items/search";
    public static final String GET_ALL_ITEMS = "/items";
    public static final String CREATE_COMMENT = "/items/{id}/comment";

    @PostMapping(CREATE_ITEM)
    public ItemDto create(
            @RequestHeader(HEADER_USER_ID)
            @NotNull(groups = Create.class)
            Integer userId,
            @RequestBody
            @Validated(Create.class)
            ItemSimpleDto itemDto) {

        return service.create(userId, itemDto);
    }

    @PatchMapping(UPDATE_ITEM)
    public ItemDto update(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable(name = "id") Integer itemId,
            @RequestBody ItemSimpleDto itemDto) {

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
            @RequestParam(name = "text") String textSearch) {

        return service.search(textSearch);
    }

    @GetMapping(GET_ALL_ITEMS)
    public List<ItemDto> getAll(
            @RequestHeader(HEADER_USER_ID) Integer userId) {

        return service.getAll(userId);
    }

    @PostMapping(CREATE_COMMENT)
    public CommentDtoRecord createComment(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable(name = "id") Integer itemId,
            @RequestBody
            @Validated(Create.class) CommentDtoSource commentDtoSource) {

        return service.createComment(userId, itemId, commentDtoSource);
    }
}
