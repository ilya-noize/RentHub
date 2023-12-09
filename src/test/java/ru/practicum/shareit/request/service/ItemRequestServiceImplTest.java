package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private final EasyRandom random = new EasyRandom();

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
        User requester = random.nextObject(User.class);
        ItemRequestSimpleDto request = random.nextObject(ItemRequestSimpleDto.class);
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
        int requesterId = random.nextInt();
        System.out.println("requesterId = " + requesterId);
        ItemRequestSimpleDto request = random.nextObject(ItemRequestSimpleDto.class);

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(requesterId, request, LocalDateTime.now()));
        System.out.println("e.getMessage() = " + e.getMessage());
        assertEquals(e.getMessage(), format(USER_WITH_ID_NOT_EXIST, requesterId));

        verify(itemRequestRepository, never())
                .save(any(ItemRequest.class));
    }

    @Test
    void get() {
        User user = random.nextObject(User.class);
        ItemRequest itemRequest = random.nextObject(ItemRequest.class);
        ItemRequestDto expected = ItemRequestMapper.INSTANCE.toDto(itemRequest);
        Item item = random.nextObject(Item.class);

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findItemsByRequestId(itemRequest.getId()))
                .thenReturn(Optional.of(List.of(item)));

        ItemRequestDto response = itemRequestService.get(user.getId(), itemRequest.getId());
        assertEquals(expected, response);
    }

    @Test
    void getAll() {
        ItemRequest itemRequest = random.nextObject(ItemRequest.class);
        List<Item> items = random.objects(Item.class, 1)
                .collect(toList());
        itemRequest.setItems(items);

        List<ItemRequestDto> expected = List.of(
                ItemRequestMapper.INSTANCE.toDto(itemRequest));

        int requesterId = itemRequest.getRequester().getId();

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNot(
                anyInt(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findItemsByRequestIn(anyList()))
                .thenReturn(items);

        List<ItemRequestDto> response = itemRequestService.getAll(requesterId,
                Pageable.ofSize(10));

        assertEquals(expected, response);
    }

    @Test
    void getAll_Throw() {
        ItemRequest itemRequest = random.nextObject(ItemRequest.class);
        int requesterId = itemRequest.getRequester().getId();

        when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService
                        .getAll(requesterId, Pageable.ofSize(10)));

        assertEquals(e.getMessage(), format(USER_WITH_ID_NOT_EXIST, requesterId));
    }

    @Test
    void getByRequesterId() {
        ItemRequest itemRequest = random.nextObject(ItemRequest.class);
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