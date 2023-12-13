package ru.practicum.shareit.item.api.service.ITest;

import org.junit.jupiter.api.DisplayName;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.constants.Constants.RANDOM;
import static ru.practicum.shareit.constants.Constants.USER_NOT_EXISTS;

@SpringBootTest
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;


    /**
     * Create new user and save in DB
     *
     * @return user with ID
     */
    private User getNewUser() {
        User owner = RANDOM.nextObject(User.class);
        return userRepository.save(owner);
    }

    /**
     * Create new item and save in DB
     *
     * @param owner Owner by Item
     * @return Item with ID
     */
    private Item getNewItem(User owner) {
        Item item = RANDOM.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        return itemRepository.save(item);
    }

    @Test
    @DisplayName("POST create<Item> - Then User not exists - Exception: " + USER_NOT_EXISTS)
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
    @DisplayName("GET get<Item> - Then User, Item exists - Return: DTO")
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
    @DisplayName("GET getAll<List<Item>> then get Items by Owner - Return: List<Dto>")
    void getAll() {
        User owner = getNewUser();

        List<Item> items = new ArrayList<>();
        final int sizeArray = 10;
        final int onPage = 1;
        for (int i = 0; i < sizeArray; i++) {
            items.add(getNewItem(owner));
        }

        final List<ItemDto> response = itemService.getAll(owner.getId(),
                Pageable.ofSize(onPage), LocalDateTime.now());

        //then
        assertEquals(response.get(0).getId(), items.get(0).getId());
        assertEquals(response.get(0).getName(), items.get(0).getName());
        assertNotEquals(sizeArray, response.size());
        assertEquals(onPage, response.size());
    }
}
