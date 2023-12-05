package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUpdateTest extends InjectResources {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Update Name + Description, not Available")
    void update_whenEditNameDescription_thenReturnDto() {
        // given
        Item entity = itemRequest;
        final int userId = 1;
        final int itemId = entity.getId();

        final String setName = "setName";
        final String setDescription = "setDescription";

        ItemSimpleDto requestDto = ItemMapper.COPY.toSimpleDto(entity);

        requestDto.setName(setName);
        requestDto.setDescription(setDescription);
        requestDto.setAvailable(null);

        String name = requestDto.getName();
        String description = requestDto.getDescription();


        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(entity));

        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        doNothing()
                .when(itemRepository)
                .updateNameAndDescriptionById(
                        anyString(),
                        anyString(),
                        anyInt());
        // when
        ItemDto response = itemService.update(userId, itemId, requestDto);
        // then
        assertNotEquals(response.getAvailable(), requestDto.getAvailable());
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
        assertEquals(entity.isAvailable(), response.getAvailable());

        assertNotNull(response.getName());
        assertNotNull(response.getDescription());
        assertNotNull(response.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(itemRepository, times(1))
                .findById(anyInt());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyInt());
        verify(itemRepository, times(1))
                .updateNameAndDescriptionById(anyString(), anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyInt());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Name + Available , not Description")
    void update_whenEditNameAvailable_thenReturnItemDto() {
        // given
        Item entity = itemRequest;
        final int userId = 1;
        final int itemId = entity.getId();

        final String setName = "setName";
        final String setDescription = "";
        final Boolean setAvailable = !entity.isAvailable();

        ItemSimpleDto requestDto = ItemMapper.COPY.toSimpleDto(entity);

        requestDto.setName(setName);
        requestDto.setDescription(setDescription);
        requestDto.setAvailable(setAvailable);

        String name = requestDto.getName();
        Boolean available = requestDto.getAvailable();


        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(entity));

        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        doNothing()
                .when(itemRepository)
                .updateNameAndAvailableById(
                        anyString(),
                        anyBoolean(),
                        anyInt());
        // when
        ItemDto response = itemService.update(userId, itemId, requestDto);
        // then
        assertNotEquals(response.getDescription(), requestDto.getDescription());
        assertEquals(name, response.getName());
        assertEquals(entity.getDescription(), response.getDescription());
        assertEquals(available, response.getAvailable());

        assertNotNull(response.getName());
        assertNotNull(response.getDescription());
        assertNotNull(response.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(itemRepository, times(1))
                .findById(anyInt());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyInt());
        verify(itemRepository, times(1))
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyInt());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Name , not Description + Available")
    void update_whenEditName_thenReturnItemDto() {
        // given
        Item entity = itemRequest;
        final int userId = 1;
        final int itemId = entity.getId();
        final String setName = "setName";
        final String setDescription = "";

        ItemSimpleDto requestDto = ItemMapper.COPY.toSimpleDto(entity);
        requestDto.setName(setName);
        requestDto.setDescription(setDescription);
        requestDto.setAvailable(null);

        String name = requestDto.getName();

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(entity));

        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        doNothing()
                .when(itemRepository)
                .updateNameById(
                        anyString(),
                        anyInt());
        // when
        ItemDto response = itemService.update(userId, itemId, requestDto);
        // then
        assertNotEquals(response.getDescription(), requestDto.getDescription());
        assertNotEquals(response.getAvailable(), requestDto.getAvailable());
        assertEquals(name, response.getName());
        assertEquals(entity.getDescription(), response.getDescription());
        assertEquals(entity.isAvailable(), response.getAvailable());

        assertNotNull(response.getName());
        assertNotNull(response.getDescription());
        assertNotNull(response.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(itemRepository, times(1))
                .findById(anyInt());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, times(1))
                .updateNameById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyInt());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Description + Available, not Name")
    void update_whenEditDescriptionAvailable_thenReturnDto() {
        // given
        Item entity = itemRequest;
        final int userId = 1;
        final int itemId = entity.getId();

        final String setName = "";
        final String setDescription = "setDescription";
        final Boolean setAvailable = !entity.isAvailable();

        ItemSimpleDto requestDto = ItemMapper.COPY.toSimpleDto(entity);

        requestDto.setName(setName);
        requestDto.setDescription(setDescription);
        requestDto.setAvailable(setAvailable);

        String description = requestDto.getDescription();
        Boolean available = requestDto.getAvailable();


        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(entity));

        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        doNothing()
                .when(itemRepository)
                .updateDescriptionAndAvailableById(
                        anyString(),
                        anyBoolean(),
                        anyInt());
        // when
        ItemDto response = itemService.update(userId, itemId, requestDto);
        // then
        assertNotEquals(response.getName(), requestDto.getName());
        assertEquals(entity.getName(), response.getName());
        assertEquals(description, response.getDescription());
        assertEquals(available, response.getAvailable());

        assertNotNull(response.getName());
        assertNotNull(response.getDescription());
        assertNotNull(response.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(itemRepository, times(1))
                .findById(anyInt());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, times(1))
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyInt());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Description, not Name + Available")
    void update_whenEditDescription_thenReturnDto() {
        // given
        Item entity = itemRequest;
        final int userId = 1;
        final int itemId = entity.getId();

        final String setName = "";
        final String setDescription = "setDescription";

        ItemSimpleDto requestDto = ItemMapper.COPY.toSimpleDto(entity);

        requestDto.setName(setName);
        requestDto.setDescription(setDescription);
        requestDto.setAvailable(null);

        String description = requestDto.getDescription();

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(entity));

        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        doNothing()
                .when(itemRepository)
                .updateDescriptionById(
                        anyString(),
                        anyInt());
        // when
        ItemDto response = itemService.update(userId, itemId, requestDto);
        // then
        assertNotEquals(response.getName(), requestDto.getName());
        assertNotEquals(response.getAvailable(), requestDto.getAvailable());
        assertEquals(entity.getName(), response.getName());
        assertEquals(description, response.getDescription());
        assertEquals(entity.isAvailable(), response.getAvailable());

        assertNotNull(response.getName());
        assertNotNull(response.getDescription());
        assertNotNull(response.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(itemRepository, times(1))
                .findById(anyInt());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, times(1))
                .updateDescriptionById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyInt());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Available, not Name + Description")
    void update_whenEditAvailable_thenReturnDto() {
        // given
        Item entity = itemRequest;
        final int userId = 1;
        final int itemId = entity.getId();

        final String setName = "";
        final String setDescription = "";
        final Boolean setAvailable = !entity.isAvailable();

        ItemSimpleDto requestDto = ItemMapper.COPY.toSimpleDto(entity);

        requestDto.setName(setName);
        requestDto.setDescription(setDescription);
        requestDto.setAvailable(setAvailable);

        Boolean available = requestDto.getAvailable();

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(entity));

        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        doNothing()
                .when(itemRepository)
                .updateAvailableById(
                        anyBoolean(),
                        anyInt());
        // when
        ItemDto response = itemService.update(userId, itemId, requestDto);
        // then
        assertNotEquals(response.getName(), requestDto.getName());
        assertNotEquals(response.getDescription(), requestDto.getDescription());
        assertEquals(entity.getName(), response.getName());
        assertEquals(entity.getDescription(), response.getDescription());
        assertEquals(available, response.getAvailable());

        assertNotNull(response.getName());
        assertNotNull(response.getDescription());
        assertNotNull(response.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(itemRepository, times(1))
                .findById(anyInt());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyInt());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyInt());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyInt());
        verify(itemRepository, times(1))
                .updateAvailableById(anyBoolean(), anyInt());
        verify(itemRepository, never()).save(any(Item.class));
    }
}