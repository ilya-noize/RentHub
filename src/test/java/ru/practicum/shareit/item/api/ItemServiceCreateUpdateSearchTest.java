package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceCreateUpdateSearchTest extends ItemServiceMasterTest {

    public static final String USER_WITH_ID_NOT_EXIST = "User with id:(%d) not exist";
    public static final String ITEM_WITH_ID_NOT_EXIST = "Item with id:(%d) not exist";

    @Test
    void create_whenSendValidItemDto_thenReturnItemDto() {
        final int userId = 1;

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(mapper.toEntity(itemDtoRequest, userId))
                .thenReturn(itemRequest);

        when(repository.save(itemRequest))
                .thenReturn(itemResponse);

        when(mapper.toDto(itemResponse))
                .thenReturn(itemDtoResponse);

        final ItemDto itemDto = service.create(userId, itemDtoRequest);

        assertEquals(itemDtoRequest.getName(), itemDto.getName());
        assertEquals(itemDtoRequest.getDescription(), itemDto.getDescription());
        assertNotNull(itemDto.getId());

        verify(mapper, times(1))
                .toEntity(itemDtoRequest, userId);
        verify(repository, times(1))
                .save(itemRequest);
        verify(mapper, times(1))
                .toDto(itemResponse);
    }

    @Test
    void create_whenUserNotExists_thenReturnException() {
        final Integer userId = 100;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.create(userId, itemDtoRequest),
                "User with id:(" + userId + ") not exist");

        verify(mapper, never())
                .toEntity(itemDtoRequest, userId);
        verify(repository, never())
                .save(itemRequest);
        verify(mapper, never())
                .toDto(itemResponse);
    }

    @Test
    void update_whenUserNotExists_thenReturnException() {
        final int userId = 100;
        final int itemId = 1;

        String name = itemRequest.getName();
        String description = itemRequest.getDescription();
        boolean available = itemRequest.isAvailable();


        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.update(userId, itemId, itemDtoRequest),
                format(USER_WITH_ID_NOT_EXIST, userId));

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(mapper, never())
                .toDto(itemResponse);
        verify(repository, never())
                .findById(anyInt());

        verify(repository, never())
                .updateNameById(name, itemId);
        verify(repository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(repository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(repository, never())
                .updateDescriptionById(description, itemId);
        verify(repository, never())
                .updateAvailableById(available, itemId);
        verify(repository, never())
                .save(itemRequest);
    }

    @Test
    void update_whenItemNotExists_thenReturnException() {
        final int userId = 1;
        final int itemId = 100;

        String name = itemRequest.getName();
        String description = itemRequest.getDescription();
        boolean available = itemRequest.isAvailable();

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(repository.findById(userId))
                .thenThrow(NotFoundException.class);
//        when(repository.notExistsByIdAndOwner_Id(itemId, userId)
//                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.update(userId, itemId, itemDtoRequest),
                format(ITEM_WITH_ID_NOT_EXIST, itemId));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(repository, atLeastOnce())
                .findById(itemId);
        verify(repository, never())
                .notExistsByIdAndOwner_Id(itemId, userId);
        verify(mapper, never())
                .toDto(itemResponse);

        verify(repository, never())
                .updateNameById(name, itemId);
        verify(repository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(repository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(repository, never())
                .updateDescriptionById(description, itemId);
        verify(repository, never())
                .updateAvailableById(available, itemId);
        verify(repository, never())
                .save(itemRequest);
    }

    @Test
    void update_whenUserNotOwnerByItem_thenReturnException() {
        final int userId = 2;
        final int itemId = 1;

        String name = itemRequest.getName();
        String description = itemRequest.getDescription();
        boolean available = itemRequest.isAvailable();


        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(repository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(repository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.update(userId, itemId, itemDtoRequest),
                "Editing an item is only allowed to the owner of that item.");

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(repository, times(1))
                .findById(anyInt());
        verify(repository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);
        verify(mapper, never())
                .toDto(itemResponse);

        verify(repository, never())
                .updateNameById(name, itemId);
        verify(repository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(repository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(repository, never())
                .updateDescriptionById(description, itemId);
        verify(repository, never())
                .updateAvailableById(available, itemId);
        verify(repository, never())
                .save(itemRequest);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Test
    // N + D + A
    void update_whenAllNotNull_thenReturnItemDto() {
        final int userId = 1;
        final int itemId = 1;

        String name = itemRequest.getName();
        String description = itemRequest.getDescription();
        boolean available = itemRequest.isAvailable();

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(repository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(repository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        when(mapper.toEntity(itemDtoRequest, userId))
                .thenReturn(itemRequest);
        when(repository.save(itemRequest))
                .thenReturn(itemResponse);
        when(mapper.toDto(itemResponse))
                .thenReturn(itemDtoResponse);

        ItemDto result = service.update(userId, itemId, itemDtoRequest);
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(repository, times(1))
                .findById(anyInt());
        verify(repository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);
        verify(mapper, times(1))
                .toEntity(itemDtoRequest, userId);
        verify(mapper, times(1))
                .toDto(itemResponse);

        verify(repository, never())
                .updateNameById(name, itemId);
        verify(repository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(repository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(repository, never())
                .updateDescriptionById(description, itemId);
        verify(repository, never())
                .updateAvailableById(available, itemId);
        verify(repository, times(1))
                .save(itemRequest);

    }

    @Test
    void search_whenSuccessSearching_thenReturnDtoList() {
        final String text = "Стол";
        final ItemSimpleDto itemResponse = itemDtoRequest;
        when(repository.searchItemByNameOrDescription(text))
                .thenReturn(anyList());

        when(mapper.toSimpleDto(itemRequest))
                .thenReturn(itemResponse);

        List<ItemSimpleDto> resultSearchList = service.search(text);
        assertTrue(resultSearchList.isEmpty()); // todo WHY? if (size > 0) TRUE else FALSE

        verify(repository, times(1))
                .searchItemByNameOrDescription(text);
        verify(mapper, never()) // todo if (size > 0) atLeastOnce() else never()
                .toDto(itemRequest);
    }

    @Test
    void search_whenFailSearching_thenReturnEmptyList() {
        String text = "qwerty";
        when(repository.searchItemByNameOrDescription(text))
                .thenReturn(List.of());
        when(mapper.toSimpleDto(any(Item.class)))
                .thenReturn(any(ItemSimpleDto.class));

        List<ItemSimpleDto> resultSearchList = service.search(text);
        assertTrue(resultSearchList.isEmpty());

        verify(repository, times(1))
                .searchItemByNameOrDescription(text);
        verify(mapper, never())
                .toDto(itemRequest);
    }
}