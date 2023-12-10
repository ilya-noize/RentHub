package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.api.service.ItemServiceImpl;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ShareItApp.RANDOM;


@ExtendWith(MockitoExtension.class)
class ItemServiceSearchTest extends InjectResources {
    private final Pageable pageable = PageRequest.of(0, 10);

    @InjectMocks
    protected ItemServiceImpl itemService;
    @Mock
    protected ItemRepository itemRepository;
    @Mock
    protected UserRepository userRepository;

    private User getNewUser() {
        User owner = RANDOM.nextObject(User.class);
        return userRepository.save(owner);
    }

    private Item getNewItem(User owner, String name, boolean available) {
        Item item = RANDOM.nextObject(Item.class);
        item.setOwner(owner);
        item.setName(name);
        item.setAvailable(available);
        item.setRequest(null);
        return item;
    }

    @Test
    void search_isBlank() {
        List<ItemSimpleDto> result = itemService.search("", pageable);

        assertTrue(result.isEmpty());

        verify(itemRepository, never())
                .searchItemByNameOrDescription("", pageable);
    }

    @Test
    void search() {
        String search = "Ty";
        User owner = RANDOM.nextObject(User.class);

        Item item1 = getNewItem(owner, "qwerty", true);
        Item item2 = getNewItem(owner, "ertyui", true);
        Item item3 = getNewItem(owner, "tyuiop", true);

        List<Item> items = List.of(item1, item2, item3);

        String searchUpperCase = search.toUpperCase();
        List<Item> expectedRepo = items.stream()
                .filter(item -> item.getName().toUpperCase()
                        .contains(searchUpperCase)
                        || item.getDescription().toUpperCase()
                        .contains(searchUpperCase))
                .collect(Collectors.toList());

        List<ItemSimpleDto> expectedService = expectedRepo.stream()
                .map(ItemMapper.INSTANCE::toSimpleDto)
                .collect(Collectors.toList());


        when(itemRepository
                .searchItemByNameOrDescription(search, pageable))
                .thenReturn(expectedRepo);

        List<ItemSimpleDto> actual = itemService.search(search, pageable);
        assertEquals(expectedService, actual);

        verify(itemRepository, times(1))
                .searchItemByNameOrDescription(search, pageable);
    }
}