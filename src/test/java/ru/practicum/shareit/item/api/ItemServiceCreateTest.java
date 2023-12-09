package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.DisplayName;
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
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;


@ExtendWith(MockitoExtension.class)
class ItemServiceCreateTest extends InjectResources { // todo true named class
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
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

        assertEquals(e.getMessage(), format(USER_WITH_ID_NOT_EXIST, userId));

        verify(itemRepository, never()).save(item);
    }
}