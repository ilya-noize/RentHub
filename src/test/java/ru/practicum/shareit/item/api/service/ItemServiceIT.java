package ru.practicum.shareit.item.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.ShareItApp.*;

@SpringBootTest
class ItemServiceIT {
    public static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;


    private User getNewUser() {
        User owner = RANDOM.nextObject(User.class);
        return userRepository.save(owner);
    }

    private Item getNewItem(User owner, ItemRequest request) {
        Item item = RANDOM.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(request);
        return itemRepository.save(item);
    }

    private ItemRequest getNewItemRequest(User requester, List<Item> items) {
        ItemRequest request = RANDOM.nextObject(ItemRequest.class);
        request.setRequester(requester);
        request.setItems(items);
        return itemRequestRepository.save(request);
    }

    @Test
    void create_requestIdNotExist_Throw() {
        //given
        User owner = getNewUser();
        Item item = getNewItem(owner, null);
        final int ownerId = owner.getId();
        ItemSimpleDto dto = ItemMapper.INSTANCE.toSimpleDto(item);
        final int requestId = RANDOM.nextInt();
        dto.setRequestId(requestId);

        //when
        assertThrows(
                NotFoundException.class,
                () -> itemService.create(ownerId, dto),
                format(REQUEST_NOT_EXISTS, requestId));
    }

    @Test
    void create_whenUserIdNotExist_thenReturnException() {
        //given
        Integer userId = 100;
        ItemSimpleDto dto = RANDOM.nextObject(ItemSimpleDto.class);
        //when
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.create(userId, dto));
        //then
        assertEquals(format(USER_NOT_EXISTS, userId),
                e.getMessage());
    }

    @Test
    void get_whenUserAndItemExists_thenReturnDto() {
        User owner = getNewUser();
        Item item = getNewItem(owner, null);

        //when
        final ItemDto response = itemService.get(owner.getId(), item.getId());

        //then
        assertEquals(response.getId(), item.getId());
        assertEquals(response.getName(), item.getName());
        assertEquals(response.getDescription(), item.getDescription());
        assertEquals(response.getAvailable(), item.isAvailable());
        assertEquals(response.getComments(), List.of());
        assertNull(response.getLastBooking());
        assertNull(response.getNextBooking());
    }

    @Test
    void getAll() {
        User owner = getNewUser();

        List<Item> items = new ArrayList<>();
        final int sizeArray = 10;
        final int onPage = 1;
        for (int i = 0; i < sizeArray; i++) {
            items.add(getNewItem(owner, null));
        }

        final List<ItemDto> response = itemService.getAll(owner.getId(),
                Pageable.ofSize(onPage), NOW);

        //then
        assertEquals(response.get(0).getId(), items.get(0).getId());
        assertEquals(response.get(0).getName(), items.get(0).getName());
        assertNotEquals(sizeArray, response.size());
        assertEquals(onPage, response.size());
    }

    @Test
    @DisplayName("UPDATE IMPOSSIBLE - Editing an item is only allowed to the owner of that item.")
    void update_notOwner() {
        User user = getNewUser();
        User owner = getNewUser();
        Item item = getNewItem(owner, null);
        final int userId = user.getId();
        final int ownerId = userId;
        final int itemId = item.getId();

        ItemSimpleDto itemDto = ItemMapper.INSTANCE.toSimpleDto(item);

        assertThrows(BadRequestException.class,
                () -> itemService.update(ownerId, itemId, itemDto),
                "Editing an item is only allowed to the owner of that item.");
    }

    @Test
    @DisplayName("UPDATE IMPOSSIBLE - Item Not Found")
    void update_itemNotExists() {
        User user = getNewUser();
        User owner = getNewUser();
        Item item = getNewItem(owner, null);
        item.setId(1);
        final int userId = user.getId();
        final int ownerId = userId;
        final int itemId = item.getId() + 1;

        ItemSimpleDto itemDto = ItemMapper.INSTANCE.toSimpleDto(item);
        itemDto.setName(null);

        assertThrows(BadRequestException.class,
                () -> itemService.update(ownerId, itemId, itemDto),
                format(ITEM_NOT_EXISTS, itemId));
    }
}
