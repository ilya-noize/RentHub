package ru.practicum.shareit.item.api;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.valid.group.Create;
import ru.practicum.shareit.valid.group.Update;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private final String headerUserId = "X-Sharer-User-Id";

    public static final String CREATE_ITEM = "/items";
    public static final String UPDATE_ITEM = "/items/{id}";
    public static final String GET_ITEM = "/items/{id}";
    public static final String SEARCH_ITEM = "/items/search";
    public static final String GET_ALL_ITEMS = "/items";

    @PostMapping(CREATE_ITEM)
    public ItemDto create(
            @RequestHeader(headerUserId)
            @NotNull(groups = Create.class)
            Integer userId,
            @RequestBody
            @Validated(Create.class)
            ItemDto itemDto) {

        return service.create(userId, itemDto);
    }

    @PatchMapping(UPDATE_ITEM)
    public ItemDto update(
            @RequestHeader(headerUserId) Integer userId,
            @PathVariable Integer id,
            @RequestBody @Validated(Update.class) ItemDto itemDto) {

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
            @RequestHeader(headerUserId) Integer userId) {

        return service.getAll(userId);
    }
}
