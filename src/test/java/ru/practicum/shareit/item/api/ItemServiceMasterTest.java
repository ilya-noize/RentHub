package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.user.api.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceMasterTest extends ItemInjectResources {
    @InjectMocks
    protected ItemServiceImpl service;
    @Mock
    protected ItemRepository repository;
    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ItemMapper mapper;

}