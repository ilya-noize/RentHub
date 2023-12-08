package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.String.format;
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
}