package ru.practicum.shareit.item.api.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.utils.InjectResources;


@ExtendWith(MockitoExtension.class)
class ItemServiceMasterTest extends InjectResources {

    @InjectMocks
    protected ItemServiceImpl itemService;
    @Mock
    protected ItemRepository itemRepository;
    @Mock
    protected ItemMapper itemMapper;

}