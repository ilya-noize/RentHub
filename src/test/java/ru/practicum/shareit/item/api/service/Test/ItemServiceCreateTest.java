package ru.practicum.shareit.item.api.service.Test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.api.service.ItemServiceImpl;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemServiceCreateTest extends InjectResources {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("ITEM CREATE _ THROW IF USER NOT EXIST")
    void create_whenUserNotExists_thenReturnException() {
        Item item = random.nextObject(Item.class);
        final User owner = item.getOwner();
        final int userId = owner.getId();
        final ItemSimpleDto request = ItemMapper.INSTANCE.toSimpleDto(item);

        when(userRepository.existsById(userId))
                .thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(userId, request));

        assertEquals(e.getMessage(), format(Constants.USER_NOT_EXISTS, userId));

        verify(itemRepository, never()).save(item);
    }

    @Test
    @DisplayName("ITEM CREATE _ THROW IF REQUEST NOT EXIST")
    void create_whenRequestNotExists_thenReturnException() {
        Item item = Constants.RANDOM.nextObject(Item.class);
        final User owner = item.getOwner();
        item.setRequest(null);
        final ItemSimpleDto simpleDto = ItemMapper.INSTANCE.toSimpleDto(item);
        final int userId = owner.getId();
        final int requestId = 1;
        simpleDto.setRequestId(requestId);

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty())
                .thenThrow(new NotFoundException(
                        format(Constants.REQUEST_NOT_EXISTS, requestId)));
        assertThrows(NotFoundException.class,
                () -> itemService.create(userId, simpleDto),
                format(Constants.REQUEST_NOT_EXISTS, requestId));

        verify(itemRepository, never()).save(item);
    }
}