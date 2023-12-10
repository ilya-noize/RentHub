package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.api.service.ItemServiceImpl;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.ShareItApp.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceGetTest extends InjectResources {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("ITEM GET _ THROW IF USER NOT EXIST")
    void get_whenUserNotExists_thenReturnException() {
        int userId = 1;
        int itemId = 1;
        when(userRepository.existsById(userId))
                .thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> itemService.get(userId, itemId),
                format(USER_NOT_EXISTS, userId));
    }


    @Test
    @DisplayName("ITEM GET _ THROW IF ITEM NOT EXIST")
    void get_whenItemNotExists_thenReturnException() {
        User owner = RANDOM.nextObject(User.class);
        int ownerId = owner.getId();
        Item item = RANDOM.nextObject(Item.class);
        item.setOwner(owner);
        int itemId = item.getId();

        when(userRepository.existsById(ownerId))
                .thenReturn(true);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty())
                .thenThrow(new NotFoundException(format(ITEM_NOT_EXISTS, itemId)));

        assertThrows(NotFoundException.class,
                () -> itemService.get(ownerId, itemId),
                format(ITEM_NOT_EXISTS, ownerId));
    }
}