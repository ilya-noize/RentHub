package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ShareItApp.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void create() {
        User requester = RANDOM.nextObject(User.class);
        ItemRequestSimpleDto request = RANDOM.nextObject(ItemRequestSimpleDto.class);
        ItemRequest entity = ItemRequestMapper.INSTANCE.toEntity(request);
        ItemRequestDto expected = ItemRequestMapper.INSTANCE.toDto(entity);

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(entity);

        ItemRequestDto response = itemRequestService.create(requester.getId(), request, LocalDateTime.now());
        assertEquals(expected, response);

        verify(itemRequestRepository, times(1))
                .save(any(ItemRequest.class));
    }


    @Test
    void create_Throw() {
        int requesterId = RANDOM.nextInt();
        System.out.println("requesterId = " + requesterId);
        ItemRequestSimpleDto request = RANDOM.nextObject(ItemRequestSimpleDto.class);

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(requesterId, request, LocalDateTime.now()));
        System.out.println("e.getMessage() = " + e.getMessage());
        assertEquals(e.getMessage(), format(USER_NOT_EXISTS, requesterId));

        verify(itemRequestRepository, never())
                .save(any(ItemRequest.class));
    }

    @Test
    void get() {
        User user = RANDOM.nextObject(User.class);
        ItemRequest itemRequest = RANDOM.nextObject(ItemRequest.class);
        itemRequest.setItems(new ArrayList<>());
        ItemRequestDto expected = ItemRequestMapper.INSTANCE.toDto(itemRequest);

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findItemsByRequestId(itemRequest.getId()))
                .thenReturn(Optional.empty());

        ItemRequestDto response = itemRequestService.get(user.getId(), itemRequest.getId());
        assertEquals(expected, response);
    }

    @Test
    void get_Throw() {
        User user = RANDOM.nextObject(User.class);
        ItemRequest itemRequest = RANDOM.nextObject(ItemRequest.class);
        itemRequest.setItems(new ArrayList<>());
        int itemRequestId = itemRequest.getId();
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.empty())
                .thenThrow(new NotFoundException(
                        format(REQUEST_NOT_EXISTS, itemRequestId)));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.get(user.getId(), itemRequestId),
                format(REQUEST_NOT_EXISTS, itemRequestId));
    }

    @Test
    void getAll() {
        ItemRequest itemRequest = RANDOM.nextObject(ItemRequest.class);
        itemRequest.setItems(new ArrayList<>());

        List<ItemRequestDto> expected = List.of(
                ItemRequestMapper.INSTANCE.toDto(itemRequest));

        int requesterId = itemRequest.getRequester().getId();

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNot(
                anyInt(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findItemsByRequestIn(anyList()))
                .thenReturn(List.of());

        List<ItemRequestDto> response = itemRequestService.getAll(requesterId,
                Pageable.ofSize(10));

        assertEquals(expected, response);
    }

    @Test
    void getAll_Throw() {
        ItemRequest itemRequest = RANDOM.nextObject(ItemRequest.class);
        int requesterId = itemRequest.getRequester().getId();

        when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAll(requesterId, Pageable.ofSize(10)),
                format(USER_NOT_EXISTS, requesterId));
    }

    @Test
    void getByRequesterId() {
        ItemRequest itemRequest = RANDOM.nextObject(ItemRequest.class);
        itemRequest.setItems(new ArrayList<>());
        int requesterId = itemRequest.getRequester().getId();
        List<ItemRequestDto> expected = List.of(
                ItemRequestMapper.INSTANCE.toDto(itemRequest));

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRequestRepository.findByRequesterId(anyInt()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> response = itemRequestService.getByRequesterId(requesterId);

        assertEquals(expected, response);
    }
}