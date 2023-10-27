package ru.practicum.shareit.item.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.api.dto.ItemDto;

import javax.validation.Valid;
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
            @RequestHeader(headerUserId) Integer userId,
            @RequestBody @Valid ItemDto itemDto) {

        return service.create(userId, itemDto);
    }

    @PatchMapping(UPDATE_ITEM)
    public ItemDto update(
            @RequestHeader(headerUserId) Integer userId,
            @PathVariable Integer id,
            @RequestBody @Valid ItemDto itemDto) {

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
