package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceCreateTest extends ItemServiceMasterTest {

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
}