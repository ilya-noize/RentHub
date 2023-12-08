package ru.practicum.shareit.item.api.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.utils.ResourcePool.*;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryIT {

    private final Pageable pageable = Pageable.ofSize(10);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        List<User> users = readResource(
                CREATE_USER_ENTITIES, new TypeReference<>() {
                });
        List<Item> items = readResource(
                CREATE_ITEM_ENTITIES, new TypeReference<>() {
                });
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
        userRepository.saveAll(users);
        itemRepository.saveAll(items);
    }

    @Test
    void searchItemByNameOrDescription() {
        //given
        String search = "оВёрТ";

        //when
        List<Item> searchItemByNameOrDescription = itemRepository.searchItemByNameOrDescription(search, pageable);

        //then
        assertEquals(2, searchItemByNameOrDescription.size());
        assertEquals(
                List.of("Шуруповёрт", "Гайковёрт"),
                searchItemByNameOrDescription.stream()
                        .map(Item::getName)
                        .collect(Collectors.toList()));
    }

    @Test
    void whenSearchItemByNameOrDescription_thenReturnListItems() {
        // given
        String search = "вЁрТ";

        //when
        List<Item> searchItems = itemRepository.searchItemByNameOrDescription(search, pageable);

        //then
        Integer size = searchItems.size();
        assertEquals(2, size);
        assertEquals("Гайковёрт", searchItems.get(size - 1).getName());
    }

    @Test
    void whenFindAllByOwnerIdEquals3_thenReturnListWithIdEquals_3_6_9() {
        //given
        int userId = 3;

        // when
        List<Item> findAllByOwnerId = itemRepository.findAllByOwner_Id(userId, pageable);

        //then
        assertEquals(3, findAllByOwnerId.size());
        assertEquals(
                List.of(3, 6, 9),
                findAllByOwnerId
                        .stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()));
    }

    @Test
    void existsByIdAndOwner_Id() {
        // User1 has only 1,4,7 Item
        //given
        int ownerId = 1;

        //then
        List<Item> itemsByOwner = itemRepository.findAllByOwner_Id(ownerId, pageable);

        //then
        assertEquals(3, itemsByOwner.size());
    }

    @Test
    void deleteByIdAndOwner_Id() {
        // given
        int ownerId = 1;
        int itemId = 7;
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
        String name = "Шуруповёрт сетевой";
        String description = "Кейс в комплекте.";
        int itemId = 3;
        Item item = itemRepository.getReferenceById(itemId);
        assertEquals("Шуруповёрт", item.getName());
        assertEquals("Работает от сети 220 Вольт. Кейс в комплекте.", item.getDescription());

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
        String name = "Шуруповёрт сетевой";
        boolean available = false;
        int itemId = 3;
        Item item = itemRepository.getReferenceById(itemId);
        assertEquals("Шуруповёрт", item.getName());
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
        String description = "Кейс в комплекте.";
        boolean available = false;
        int itemId = 3;
        Item item = itemRepository.getReferenceById(itemId);
        assertEquals("Работает от сети 220 Вольт. Кейс в комплекте.", item.getDescription());
        assertTrue(item.isAvailable());

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
        String name = "Шуруповёрт сетевой";
        int itemId = 3;
        Item item = itemRepository.getReferenceById(itemId);
        assertEquals("Шуруповёрт", item.getName());

        //when
        itemRepository.updateNameById(name, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertEquals(itemUpdate.getName(), name);
    }

    @Test
    void updateDescriptionById() {
        //given
        String description = "Кейс в комплекте.";
        int itemId = 3;
        Item item = itemRepository.getReferenceById(itemId);
        assertEquals("Работает от сети 220 Вольт. Кейс в комплекте.", item.getDescription());

        //when
        itemRepository.updateDescriptionById(description, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertEquals(itemUpdate.getDescription(), description);
    }

    @Test
    void updateAvailableById() {
        //given
        boolean available = false;
        int itemId = 3;

        //when
        itemRepository.updateAvailableById(available, itemId);

        //then
        Item itemUpdate = itemRepository.getReferenceById(itemId);
        assertFalse(itemUpdate.isAvailable());
    }
}