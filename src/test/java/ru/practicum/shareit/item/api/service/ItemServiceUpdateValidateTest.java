package ru.practicum.shareit.item.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.utils.InjectResources;

import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ShareItApp.ITEM_WITH_ID_NOT_EXIST;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;


@ExtendWith(MockitoExtension.class)
class ItemServiceUpdateValidateTest extends InjectResources {

    @InjectMocks
    protected ItemServiceImpl itemService;
    @Mock
    protected ItemRepository itemRepository;
    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ItemMapper itemMapper;

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
                () -> itemService.update(userId, itemId, itemDtoRequest),
                format(USER_WITH_ID_NOT_EXIST, userId));

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(itemMapper, never())
                .toDto(itemResponse);
        verify(itemRepository, never())
                .findById(anyInt());

        verify(itemRepository, never())
                .updateNameById(name, itemId);
        verify(itemRepository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(itemRepository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(itemRepository, never())
                .updateDescriptionById(description, itemId);
        verify(itemRepository, never())
                .updateAvailableById(available, itemId);
        verify(itemRepository, never())
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
        lenient().when(itemRepository.findById(userId))
                .thenThrow(NotFoundException.class);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(userId, itemId, itemDtoRequest));
        assertEquals(exception.getMessage(),
                format(ITEM_WITH_ID_NOT_EXIST, itemId));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRepository, atLeastOnce())
                .findById(itemId);
        verify(itemRepository, never())
                .notExistsByIdAndOwner_Id(itemId, userId);
        verify(itemMapper, never())
                .toDto(itemResponse);

        verify(itemRepository, never())
                .updateNameById(name, itemId);
        verify(itemRepository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(itemRepository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(itemRepository, never())
                .updateDescriptionById(description, itemId);
        verify(itemRepository, never())
                .updateAvailableById(available, itemId);
        verify(itemRepository, never())
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

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> itemService.update(userId, itemId, itemDtoRequest),
                "Editing an item is only allowed to the owner of that item.");

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(itemRepository, times(1))
                .findById(anyInt());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);
        verify(itemMapper, never())
                .toDto(itemResponse);

        verify(itemRepository, never())
                .updateNameById(name, itemId);
        verify(itemRepository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(itemRepository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(itemRepository, never())
                .updateDescriptionById(description, itemId);
        verify(itemRepository, never())
                .updateAvailableById(available, itemId);
        verify(itemRepository, never())
                .save(itemRequest);
    }

//    @Test
//    @DisplayName("Update Entity!")
//    void update_whenEditEntity_thenReturnDto() {
//        // given
//        Item entity = itemRequest;
//        final int userId = 1;
//        final int itemId = entity.getId();
//
//        final String setName = "setName";
//        final String setDescription = "setDescription";
//        final Boolean setAvailable = !entity.isAvailable();
//
//        ItemSimpleDto requestDto = ItemMapper.COPY.toSimpleDto(entity);
//
//        requestDto.setName(setName);
//        requestDto.setDescription(setDescription);
//        requestDto.setAvailable(setAvailable);
//
//        Boolean available = requestDto.getAvailable();
//
//        when(userRepository.existsById(userId))
//                .thenReturn(true);
//        when(itemRepository.findById(itemId))
//                .thenReturn(Optional.of(entity));
//        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
//                .thenReturn(false);
//
//        when(itemMapper.toEntity(requestDto, userId))
//                .thenReturn(entity);
//        lenient().when(itemRepository.save(entity))
//                .thenReturn(entity);
//        when(itemMapper.toDto(entity))
//                .thenReturn(itemDtoResponse);
//
//        // when
//        ItemDto response = itemService.update(userId, itemId, requestDto);
//        // then
//        assertNotEquals(response.getName(), requestDto.getName());
//        assertNotEquals(response.getDescription(), requestDto.getDescription());
//        assertEquals(entity.getName(), response.getName());
//        assertEquals(entity.getDescription(), response.getDescription());
//        assertEquals(available, response.getAvailable());
//
//        assertNotNull(response.getName());
//        assertNotNull(response.getDescription());
//        assertNotNull(response.getAvailable());
//
//        verify(userRepository, times(1))
//                .existsById(anyInt());
//        verify(itemRepository, times(1))
//                .findById(anyInt());
//        verify(itemRepository, times(1))
//                .notExistsByIdAndOwner_Id(itemId, userId);
//
//        verify(itemRepository, never())
//                .updateNameById(anyString(), anyInt());
//        verify(itemRepository, never())
//                .updateNameAndDescriptionById(anyString(), anyString(), anyInt());
//        verify(itemRepository, never())
//                .updateNameAndAvailableById(anyString(), anyBoolean(), anyInt());
//        verify(itemRepository, never())
//                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyInt());
//        verify(itemRepository, never())
//                .updateDescriptionById(anyString(), anyInt());
//        verify(itemRepository, never())
//                .updateAvailableById(anyBoolean(), anyInt());
//        verify(itemRepository, times(1)).save(any(Item.class));
//    }
}