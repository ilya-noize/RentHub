package ru.practicum.shareit.item.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.utils.InjectResources;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceCreateTest extends InjectResources {
    @InjectMocks
    protected ItemServiceImpl itemService;
    @Mock
    protected ItemRepository itemRepository;
    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ItemMapper itemMapper;

//    @Test
//    void create_whenSendValidItemDto_thenReturnItemDto() {
//        final int userId = 1;
//        Item entity = itemRequest;
//        ItemSimpleDto request = ItemMapper.COPY.toSimpleDto(entity);
//        ItemDto expected = ItemMapper.COPY.toDto(entity);
//
//        when(userRepository.existsById(userId))
//                .thenReturn(true);
//
//        when(itemMapper.toEntity(request, userId))
//                .thenReturn(entity);
//
//        when(itemRepository.save(entity))
//                .thenReturn(entity);
//
//        when(itemMapper.toDto(entity))
//                .thenReturn(expected);
//
//        final ItemDto response = itemService.create(userId, request);
//
//        assertEquals(request.getName(), response.getName());
//        assertEquals(request.getDescription(), response.getDescription());
//        assertNotNull(response.getId());
//
//        verify(itemMapper, times(1))
//                .toEntity(itemDtoRequest, userId);
//        verify(itemRepository, times(1))
//                .save(itemRequest);
//        verify(itemMapper, times(1))
//                .toDto(itemResponse);
//    }

    @Test
    void create_whenUserNotExists_thenReturnException() {
        final Integer userId = 100;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemService.create(userId, itemDtoRequest),
                "User with id:(" + userId + ") not exist");

        verify(itemMapper, never())
                .toEntity(itemDtoRequest, userId);
        verify(itemRepository, never())
                .save(itemRequest);
        verify(itemMapper, never())
                .toDto(itemResponse);
    }
}