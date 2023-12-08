package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.api.service.ItemService;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;

@SpringBootTest
class ItemServiceTest extends InjectResources { // todo true named class
    public static final LocalDateTime NOW = LocalDateTime.now();
    private final List<Integer> userIds = new ArrayList<>();
    private final List<Integer> itemIds = new ArrayList<>();
    private ItemSimpleDto itemSimpleDto;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        boolean getUserSimpleDto = true;
        boolean getItemSimpleDto = true;


        items.forEach(item -> {
            int itemId = item.getId();
            if (itemId % 3 == 1) {
                item.setOwner(users.get(0));
            } else if (itemId % 3 == 2) {
                item.setOwner(users.get(1));
            } else {
                item.setOwner(users.get(2));
            }
        });

        for (User owner : ownerStorage.keySet()) {

            UserSimpleDto userDto = new UserSimpleDto(owner.getEmail(), owner.getName());

            if (getUserSimpleDto) {
                getUserSimpleDto = false;
            }
            UserDto resultUser = userService.create(userDto);
            int userId = resultUser.getId();

            List<Integer> itemIdsToStorage = new ArrayList<>();

            for (Item item : ownerStorage.get(owner)) {

                ItemSimpleDto itemDto = ItemSimpleDto.builder()
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.isAvailable()).build();

                if (getItemSimpleDto) {
                    itemSimpleDto = itemDto;
                    getItemSimpleDto = false;
                }
                ItemDto resultItem = itemService.create(userId, itemDto);
                int itemId = resultItem.getId();
                itemIdsToStorage.add(itemId);
            }
            itemIds.addAll(itemIdsToStorage);
            userIds.add(userId);
        }
        System.out.printf("userIds:%s, itemIds:%s%n", userIds, itemIds);
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
    void create_whenUserIdNotExist_thenReturnException() {
        //given
        Integer userId = 100;
        //when
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.create(userId, itemSimpleDto));
        //then
        assertEquals(format(USER_WITH_ID_NOT_EXIST, userId),
                e.getMessage());
    }

    @Test
    void get_whenUserAndItemExists_thenReturnDto() {
        //given
        int userId = userIds.get(0);
        int itemId = itemIds.get(0);

        //when
        final ItemDto response = itemService.get(userId, itemId);

        //then
        assertEquals(response.getId(), itemId);
        assertEquals(response.getName(), itemSimpleDto.getName());
        assertEquals(response.getDescription(), itemSimpleDto.getDescription());
        assertEquals(response.getAvailable(), itemSimpleDto.getAvailable());
        assertEquals(response.getComments(), List.of());
        assertNull(response.getLastBooking());
        assertNull(response.getNextBooking());
    }

    @Test
    void get_whenUserNotExist_thenReturnException() {
        //given
        int userId = 9999;
        int itemId = itemIds.get(itemIds.size() - 1);

        //when
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.get(userId, itemId));

        //then
        assertEquals(exception.getMessage(),
                format(USER_WITH_ID_NOT_EXIST, userId));
    }

    @DirtiesContext
    @Test
    void getAll() {
        // when
        List<ItemDto> getAllByUser1 = itemService
                .getAll(userIds.get(0), PageRequest.of(0, 50), NOW);
        List<ItemDto> getAll = itemService
                .getAll(userIds.get(2), PageRequest.of(0, 50), NOW);

        // then
        assertEquals(3, getAllByUser1.size());
        assertEquals(3, getAll.size());
    }
}
