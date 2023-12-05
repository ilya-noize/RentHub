package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.api.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemServiceSearchIT extends InjectResources {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;


    @Test
    void search() {
        //given
        UserDto user = userService.create(userDtoRequest);
        final int userId = user.getId();

        for (ItemSimpleDto itemSimpleDto : itemDtoList) {
            itemService.create(userId, itemSimpleDto);
        }

        //when
        final List<ItemSimpleDto> searchNothing = itemService.search("");
        final List<ItemSimpleDto> searchEmpty = itemService.search("Empty");

        //then
        assertEquals(List.of(), searchNothing);
        assertEquals(List.of(), searchEmpty);
    }
}