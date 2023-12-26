package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.constants.Constants;
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
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        User requester = Constants.RANDOM.nextObject(User.class);
        ItemRequestSimpleDto request = Constants.RANDOM.nextObject(ItemRequestSimpleDto.class);
        ItemRequest entity = ItemRequestMapper.INSTANCE.toEntity(request, requester.getId());
        ItemRequestDto expected = ItemRequestMapper.INSTANCE.toDto(entity);

        when(userRepository.findById(anyLong()))
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
        long requesterId = Constants.RANDOM.nextInt();
        System.out.println("requesterId = " + requesterId);
        ItemRequestSimpleDto request = Constants.RANDOM.nextObject(ItemRequestSimpleDto.class);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(requesterId, request, LocalDateTime.now()));
        System.out.println("e.getMessage() = " + e.getMessage());
        assertEquals(e.getMessage(), format(Constants.USER_NOT_EXISTS, requesterId));

        verify(itemRequestRepository, never())
                .save(any(ItemRequest.class));
    }

    @Test
    void get() {
        User user = Constants.RANDOM.nextObject(User.class);
        ItemRequest itemRequest = Constants.RANDOM.nextObject(ItemRequest.class);
        itemRequest.setItems(new ArrayList<>());
        ItemRequestDto expected = ItemRequestMapper.INSTANCE.toDto(itemRequest);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.getByRequest_Id(itemRequest.getId()))
                .thenReturn(emptyList());

        ItemRequestDto response = itemRequestService.get(user.getId(), itemRequest.getId());
        assertEquals(expected, response);
    }

    @Test
    void get_Throw() {
        User user = Constants.RANDOM.nextObject(User.class);
        ItemRequest itemRequest = Constants.RANDOM.nextObject(ItemRequest.class);
        itemRequest.setItems(new ArrayList<>());
        long itemRequestId = itemRequest.getId();
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.empty())
                .thenThrow(new NotFoundException(
                        format(Constants.REQUEST_NOT_EXISTS, itemRequestId)));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.get(user.getId(), itemRequestId),
                format(Constants.REQUEST_NOT_EXISTS, itemRequestId));
    }

    @Test
    void getAll() {
        ItemRequest itemRequest = Constants.RANDOM.nextObject(ItemRequest.class);
        itemRequest.setItems(new ArrayList<>());

        List<ItemRequestDto> expected = List.of(
                ItemRequestMapper.INSTANCE.toDto(itemRequest));

        long requesterId = itemRequest.getRequester().getId();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNot(
                anyLong())) // todo , any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestIn(anyList()))
                .thenReturn(List.of());

        List<ItemRequestDto> response = itemRequestService.getAll(requesterId,
                Pageable.ofSize(10));

        assertEquals(expected, response);
    }

    @Test
    void getAll_Throw() {
        ItemRequest itemRequest = Constants.RANDOM.nextObject(ItemRequest.class);
        long requesterId = itemRequest.getRequester().getId();

        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAll(requesterId, Pageable.ofSize(10)),
                format(Constants.USER_NOT_EXISTS, requesterId));
    }

    @Test
    void getByRequesterId() {
        ItemRequest itemRequest = Constants.RANDOM.nextObject(ItemRequest.class);
        itemRequest.setItems(new ArrayList<>());
        long requesterId = itemRequest.getRequester().getId();
        List<ItemRequestDto> expected = List.of(
                ItemRequestMapper.INSTANCE.toDto(itemRequest));

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findByRequesterId(anyLong()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> response = itemRequestService.getByRequesterId(requesterId);

        assertEquals(expected, response);
    }
}