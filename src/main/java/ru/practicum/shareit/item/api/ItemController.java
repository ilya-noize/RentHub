package ru.practicum.shareit.item.api;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.api.dto.CommentDto;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

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
            ItemDto itemDto) {

        return service.create(userId, itemDto);
    }

    @PatchMapping(UPDATE_ITEM)
    public ItemDto update(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Integer id,
            @RequestBody @Validated(Update.class) ItemDto itemDto
            ) {

        return service.update(userId, id, itemDto);
    }

    @GetMapping(GET_ITEM)
    public ItemDto get(@PathVariable Integer id) {

        return service.get(id);
    }

    @GetMapping(SEARCH_ITEM)
    public List<ItemDto> search(
            @RequestParam(name = "text") String textSearch) {

        return service.search(textSearch);
    }

    @GetMapping(GET_ALL_ITEMS)
    public List<ItemDto> getAll(
            @RequestHeader(HEADER_USER_ID) Integer userId) {

        return service.getAll(userId);
    }

    @PostMapping(CREATE_COMMENT)
    public CommentDto createComment(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Integer id,
            @RequestBody String comment) {
        return service.createComment(userId, id, comment);
    }
}
