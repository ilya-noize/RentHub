package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.api.service.ItemService;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;
import static ru.practicum.shareit.ShareItApp.random;

@SpringBootTest
class ItemServiceTest { // todo true named class
    public static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;


    private User getNewUser() {
        User owner = random.nextObject(User.class);
        return userRepository.save(owner);
    }

    private Item getNewItem(User owner) {
        Item item = random.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        return itemRepository.save(item);
    }

    @Test
    void create_whenUserIdNotExist_thenReturnException() {
        //given
        Integer userId = 100;
        ItemSimpleDto dto = random.nextObject(ItemSimpleDto.class);
        //when
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.create(userId, dto));
        //then
        assertEquals(format(USER_WITH_ID_NOT_EXIST, userId),
                e.getMessage());
    }

    @Test
    void get_whenUserAndItemExists_thenReturnDto() {
        User owner = getNewUser();
        Item item = getNewItem(owner);

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
            items.add(getNewItem(owner));
        }

        final List<ItemDto> response = itemService.getAll(owner.getId(),
                Pageable.ofSize(onPage), NOW);

        //then
        assertEquals(response.get(0).getId(), items.get(0).getId());
        assertEquals(response.get(0).getName(), items.get(0).getName());
        assertNotEquals(sizeArray, response.size());
        assertEquals(onPage, response.size());
    }
}
