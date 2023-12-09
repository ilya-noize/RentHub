package ru.practicum.shareit.item.api.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.ShareItApp.random;


@DataJpaTest
public class ItemRepositoryIT {

    private final Pageable pageable = Pageable.ofSize(10);

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private List<User> getListUsers() {
        List<User> users = random.objects(User.class, 3).collect(toList());
        return userRepository.saveAll(users);
    }

    private List<Item> getListItems(List<User> users) {
        List<Item> items = random.objects(Item.class, 9).collect(toList());

        for (int i = 1; i <= items.size(); i++) {
            items.get(i - 1).setId(i);
            items.get(i - 1).setRequest(null);
        }

        items.forEach(item -> {
            int itemId = item.getId();
            if (itemId % 3 == 1) {
                item.setOwner(users.get(0));
                item.setName("Столярные инструменты");
            } else if (itemId % 3 == 2) {
                item.setOwner(users.get(1));
                item.setDescription("Сетевой шуруповёрт");
            } else {
                item.setOwner(users.get(2));
                item.setAvailable(false);
            }
        });
        return itemRepository.saveAll(items);
    }

    private void setUp() {
        List<User> users = getListUsers();
        getListItems(users);
    }

    @Test
    void searchItemByNameOrDescription() {
        setUp();
        //given
        String search = "оВёрТ";

        //when
        List<Item> searchItemByNameOrDescription = itemRepository.searchItemByNameOrDescription(search, pageable);

        //then
        assertEquals(1, searchItemByNameOrDescription.size());
    }

    @Test
    void existsByIdAndOwner_Id() {
        //given
        List<User> users = getListUsers();
        getListItems(users);
        int ownerId = users.get(0).getId();

        //then
        List<Item> itemsByOwner = itemRepository.findAllByOwner_Id(ownerId, pageable);

        //then
        assertEquals(3, itemsByOwner.size());
    }

    @Test
    void deleteByIdAndOwner_Id() {
        // given
        List<User> users = getListUsers();
        List<Item> items = getListItems(users);
        int ownerId = users.get(0).getId();
        int itemId = items.get(0).getId();

        List<Item> itemsBefore = itemRepository.findAllByOwner_Id(ownerId, pageable);
        assertEquals(3, itemsBefore.size());

        //when
        itemRepository.deleteByIdAndOwner_Id(itemId, ownerId);

        //then
        List<Item> itemsAfter = itemRepository.findAllByOwner_Id(ownerId, pageable);
        assertEquals(2, itemsAfter.size());
    }

    @Test
    void updateNameAndDescriptionById() {
        //given
        List<User> users = getListUsers();
        List<Item> items = getListItems(users);
        int itemId = items.get(0).getId();

        String name = "Шуруповёрт сетевой";
        String description = "Кейс в комплекте.";

        //when
        itemRepository.updateNameAndDescriptionById(name, description, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertEquals(itemUpdate.getName(), name);
        assertEquals(itemUpdate.getDescription(), description);
    }

    @Test
    void updateNameAndAvailableById() {
        //given
        List<User> users = getListUsers();
        List<Item> items = getListItems(users);
        int itemId = items.get(0).getId();

        String name = "Шуруповёрт сетевой";
        boolean available = false;

        Item item = itemRepository.getReferenceById(itemId);
        assertTrue(item.isAvailable());

        //when
        itemRepository.updateNameAndAvailableById(name, available, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertEquals(itemUpdate.getName(), name);
        assertFalse(itemUpdate.isAvailable());
    }

    @Test
    void updateDescriptionAndAvailableById() {
        //given
        List<User> users = getListUsers();
        List<Item> items = getListItems(users);
        int itemId = items.get(0).getId();

        String description = "Кейс в комплекте.";
        boolean available = false;

        //when
        itemRepository.updateDescriptionAndAvailableById(description, available, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertEquals(itemUpdate.getDescription(), description);
        assertFalse(itemUpdate.isAvailable());
    }

    @Test
    void updateNameById() {
        //given
        List<User> users = getListUsers();
        List<Item> items = getListItems(users);
        int itemId = items.get(0).getId();

        String name = "Шуруповёрт сетевой";

        //when
        itemRepository.updateNameById(name, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertEquals(itemUpdate.getName(), name);
    }

    @Test
    void updateDescriptionById() {
        //given
        List<User> users = getListUsers();
        List<Item> items = getListItems(users);
        int itemId = items.get(0).getId();

        String description = "Кейс в комплекте.";

        //when
        itemRepository.updateDescriptionById(description, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertEquals(itemUpdate.getDescription(), description);
    }

    @Test
    void updateAvailableById() {
        //given
        List<User> users = getListUsers();
        List<Item> items = getListItems(users);
        int itemId = items.get(0).getId();

        boolean available = false;

        //when
        itemRepository.updateAvailableById(available, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertFalse(itemUpdate.isAvailable());
    }
}