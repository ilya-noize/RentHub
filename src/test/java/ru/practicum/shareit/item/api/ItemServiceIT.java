package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.api.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.ShareItApp.ITEM_WITH_ID_NOT_EXIST;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;

@SpringBootTest
class ItemServiceIT extends InjectResources {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private List<Integer> getItemIdsByUser(List<ItemDto> itemsByUser1) {
        return itemsByUser1
                .stream()
                .map(ItemDto::getId)
                .collect(toList());
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        userService.getAll()
                .stream()
                .map(UserDto::getId)
                .forEach(userService::delete);
    }

    @Test
    void create_whenAllFieldsDtoNotNull_thenReturnDto() {
        //given
        UserDto user = userService.create(userDtoRequest);
        assertEquals("user1", user.getName());
        Integer userId = user.getId();
        assertNotNull(userId);
        //when
        ItemDto itemDtoResponse = itemService.create(userId, itemDtoRequest);
        //then
        assertNotNull(itemDtoResponse.getId());
        assertEquals(itemDtoRequest.getName(),
                itemDtoResponse.getName());
        assertEquals(itemDtoRequest.getDescription(),
                itemDtoResponse.getDescription());
        assertEquals(itemDtoRequest.getAvailable(),
                itemDtoResponse.getAvailable());
        assertNull(itemDtoResponse.getLastBooking());
        assertNull(itemDtoResponse.getNextBooking());
        assertNull(itemDtoResponse.getComments());
    }

    @Test
    void create_whenUserIdIsNull_thenReturnException() {
        //given
        Integer userId = null;
        //when
        InvalidDataAccessApiUsageException e = assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> itemService.create(userId, itemDtoRequest));
        //then
        assertTrue(requireNonNull(e.getMessage()).contains("The given id must not be null!"));
    }

    @Test
    void create_whenUserIdNotExist_thenReturnException() {
        //given
        Integer userId = 100;
        //when
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.create(userId, itemDtoRequest));
        //then
        assertEquals(format(USER_WITH_ID_NOT_EXIST, userId),
                e.getMessage());
    }

    @Test
    void updateAllFieldsDtoNotNull_thenReturnDto() {
        //given
        UserDto user = userService.create(userDtoRequest);
        final Integer userId = user.getId();
        assertNotNull(userId);
        assertEquals("user1", user.getName());

        ItemDto itemCreate = itemService.create(userId, itemDtoRequest);
        final Integer itemId = itemCreate.getId();
        assertEquals("Стол", itemDtoRequest.getName());

        //when
        final ItemDto itemUpdate = itemService.update(userId, itemId,
                ItemSimpleDto.builder().name("Стол обеденный").build());

        //then
        assertEquals(itemId, itemUpdate.getId());
        assertEquals("Стол обеденный", itemUpdate.getName());
        assertEquals(itemDtoRequest.getDescription(),
                itemUpdate.getDescription());
        assertEquals(itemDtoRequest.getAvailable(),
                itemUpdate.getAvailable());
        assertNull(itemUpdate.getLastBooking());
        assertNull(itemUpdate.getNextBooking());
        assertNull(itemUpdate.getComments());
    }

    @Test
    void get_whenUserAndItemExists_thenReturnDto() {
        //given
        UserDto user = userService.create(userDtoRequest);
        final Integer userId = user.getId();
        assertNotNull(userId);
        assertEquals("user1", user.getName());

        ItemDto itemCreate = itemService.create(userId, itemDtoRequest);
        final Integer itemId = itemCreate.getId();

        //when
        final ItemDto itemGet = itemService.get(userId, itemId);

        //then
        assertEquals(itemId,
                itemGet.getId());
        assertEquals(itemDtoRequest.getName(),
                itemGet.getName());
        assertEquals(itemDtoRequest.getDescription(),
                itemGet.getDescription());
        assertEquals(itemDtoRequest.getAvailable(),
                itemGet.getAvailable());
        assertEquals(List.of(),
                itemGet.getComments());
        assertNull(itemGet.getLastBooking());
        assertNull(itemGet.getNextBooking());
    }

    @Test
    void get_whenUserNotExist_thenReturnException() {
        //given
        UserDto user = userService.create(userDtoRequest);
        final Integer userId = user.getId();
        assertNotNull(userId);
        ItemDto itemCreate = itemService.create(userId, itemDtoRequest);
        final Integer itemId = itemCreate.getId();

        final Integer userNotExistId = 100;

        //when
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.get(userNotExistId, itemId));

        //then
        assertEquals(exception.getMessage(),
                format(USER_WITH_ID_NOT_EXIST, userNotExistId));
    }

    @Test
    void get_whenItemNotExist_thenReturnException() {
        //given
        UserDto user = userService.create(userDtoRequest);
        final Integer userId = user.getId();
        assertNotNull(userId);
        itemService.create(userId, itemDtoRequest);

        final Integer itemNotExistId = 100;

        //when
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.get(userId, itemNotExistId));

        //then
        assertEquals(exception.getMessage(),
                format(ITEM_WITH_ID_NOT_EXIST, itemNotExistId));
    }

    @DirtiesContext
    @Test
    void getAll() {
        //given
        UserDto user1 = userService.create(userDtoRequest);
        userDtoRequest.setEmail("user2@user.com");
        userDtoRequest.setName("user2");
        UserDto user2 = userService.create(userDtoRequest);

        List<Integer> userIds = List.of(user1.getId(), user2.getId());

        List<ItemDto> itemsByUser1 = new ArrayList<>();
        List<ItemDto> itemsByUser2 = new ArrayList<>();
        for (int i = 0; i < itemDtoList.size(); i++) {
            if (i < 5) {
                itemsByUser1.add(
                        itemService.create(userIds.get(0), itemDtoList.get(i)));
            } else {
                itemsByUser2.add(
                        itemService.create(userIds.get(1), itemDtoList.get(i)));
            }
        }

        // when
        List<ItemDto> getAllByUser1 = itemService.getAll(userIds.get(0));
        List<ItemDto> getAllByUser2 = itemService.getAll(userIds.get(1));
        List<ItemDto> getAll = itemService.getAll(null);

        // then
        assertEquals(5, getAllByUser1.size());
        assertEquals(itemsByUser1, getAllByUser1);
        assertEquals(4, getAllByUser2.size());
        assertEquals(itemsByUser2, getAllByUser2);
        assertEquals(0, getAll.size());
    }

    @Test
    void delete() {
        //given
        UserDto user1 = userService.create(userDtoRequest);

        userDtoRequest.setEmail("user2@user.com");
        userDtoRequest.setName("user2");
        UserDto user2 = userService.create(userDtoRequest);

        final List<Integer> userIds = List.of(user1.getId(), user2.getId());

        List<ItemDto> itemsByUser1 = new ArrayList<>();
        List<ItemDto> itemsByUser2 = new ArrayList<>();
        for (int i = 0; i < itemDtoList.size(); i++) {
            if (i < 5) {
                itemsByUser1.add(
                        itemService.create(userIds.get(0), itemDtoList.get(i)));
            } else {
                itemsByUser2.add(
                        itemService.create(userIds.get(1), itemDtoList.get(i)));
            }
        }
        final List<Integer> itemIdsByUser1 = getItemIdsByUser(itemsByUser1);
        final Integer unknown = -1;

        // when
//        itemService.delete(userIds.get(0), itemsByUser1.get(1).getId());
//        itemService.delete(userIds.get(1), itemsByUser2.get(1).getId());
        NotFoundException notFoundUser = assertThrows(NotFoundException.class,
                () -> itemService.delete(unknown, itemIdsByUser1.get(0)));
        NotFoundException notFoundItem = assertThrows(NotFoundException.class,
                () -> itemService.delete(userIds.get(0), unknown));

        // then
        assertEquals(5, itemsByUser1.size());
        assertEquals(4, itemsByUser2.size());
        assertEquals(format(USER_WITH_ID_NOT_EXIST, unknown),
                notFoundUser.getMessage());
        assertEquals(format(ITEM_WITH_ID_NOT_EXIST, unknown),
                notFoundItem.getMessage());
    }

}