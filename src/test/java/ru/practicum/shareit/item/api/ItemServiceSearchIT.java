package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.service.ItemService;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemServiceSearchIT extends InjectResources {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        for (User owner : ownerStorage.keySet()) {

            int userId = userService.create(new UserSimpleDto(owner.getEmail(), owner.getName())).getId();

            for (Item item : ownerStorage.get(owner)) {

                ItemSimpleDto itemDto = ItemSimpleDto.builder()
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.isAvailable()).build();

                itemService.create(userId, itemDto);
            }
        }
    }

    @Test
    void search() {
        //given
        String search = "Стол";
        String empty = "";
        Pageable pageable = Pageable.ofSize(10);

        //when
        final List<ItemSimpleDto> searchTable = itemService.search(search, pageable);
        final List<ItemSimpleDto> searchEmpty = itemService.search(empty, pageable);

        //then
        assertEquals(3, searchTable.size());
        assertEquals(List.of(), searchEmpty);
    }
}