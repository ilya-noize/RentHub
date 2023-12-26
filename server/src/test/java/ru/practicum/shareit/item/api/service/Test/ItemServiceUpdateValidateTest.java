package ru.practicum.shareit.item.api.service.Test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.api.service.ItemServiceImpl;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.utils.InjectResources;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.constants.Constants.USER_NOT_EXISTS;


@ExtendWith(MockitoExtension.class)
class ItemServiceUpdateValidateTest extends InjectResources {
    private final Item itemRequest = items.get(1);
    private final Item itemResponse = itemRequest;
    @InjectMocks
    protected ItemServiceImpl itemService;
    @Mock
    protected ItemRepository itemRepository;
    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ItemMapper itemMapper;
    private ItemSimpleDto itemDtoRequest;

    @Test
    void update_whenUserNotExists_thenReturnException() {
        final long userId = 100;
        final long itemId = 1;

        String name = itemRequest.getName();
        String description = itemRequest.getDescription();
        boolean available = itemRequest.isAvailable();

        itemDtoRequest = ItemSimpleDto.builder()
                .id(itemRequest.getId())
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.isAvailable()).build();


        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemService.update(userId, itemId, itemDtoRequest),
                format(USER_NOT_EXISTS, userId));

        verify(userRepository, times(1))
                .existsById(anyLong());
        verify(itemMapper, never())
                .toDto(itemResponse);
        verify(itemRepository, never())
                .findById(anyLong());

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


}