package ru.practicum.shareit.item.api.service.Test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.dto.ItemMapper;
import ru.practicum.shareit.item.api.dto.ItemSimpleDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.api.service.ItemServiceImpl;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.constants.Constants.ITEM_NOT_EXISTS;
import static ru.practicum.shareit.constants.Constants.USER_NOT_EXISTS;

@ExtendWith(MockitoExtension.class)
class ItemServiceUpdateTest extends InjectResources {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Update impossible - Owner not found")
    void update_whenUserNotExists_thenReturnException() {
        User owner = random.nextObject(User.class);
        owner.setId(10L);
        Item item = random.nextObject(Item.class);
        item.setOwner(owner);
        final long wrongUserId = random.nextInt(5);
        final long itemId = item.getId();

        ItemSimpleDto itemDtoRequest = ItemMapper.INSTANCE.toSimpleDto(item);
        String name = itemDtoRequest.getName();
        String description = itemDtoRequest.getDescription();
        Boolean available = itemDtoRequest.getAvailable();

        when(userRepository.existsById(wrongUserId))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemService.update(wrongUserId, itemId, itemDtoRequest),
                format(USER_NOT_EXISTS, wrongUserId));

        verify(userRepository, times(1))
                .existsById(anyLong());
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

        verify(itemRepository, never()).save(item);
    }

    @Test
    @DisplayName("Update impossible - Item not found")
    void update_whenItemNotExists_thenReturnException() {

        User owner = random.nextObject(User.class);
        owner.setId(10L);
        Item item = random.nextObject(Item.class);
        final long itemId = item.getId();

        item.setOwner(owner);
        final long userId = random.nextInt(5);

        ItemSimpleDto itemDtoRequest = ItemMapper.INSTANCE.toSimpleDto(item);
        String name = itemDtoRequest.getName();
        String description = itemDtoRequest.getDescription();
        Boolean available = itemDtoRequest.getAvailable();

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty())
                .thenThrow(new NotFoundException(
                        format(ITEM_NOT_EXISTS, itemId)));

        assertThrows(NotFoundException.class,
                () -> itemService.update(userId, itemId, itemDtoRequest),
                format(ITEM_NOT_EXISTS, userId));

        verify(userRepository, times(1))
                .existsById(anyLong());
        verify(itemRepository, times(1))
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

        verify(itemRepository, never()).save(item);

    }

    @Test
    @DisplayName("Update Name + Description + Available")
    void update() {
        // given
        User owner = random.nextObject(User.class);
        final long userId = owner.getId();
        Item entity = random.nextObject(Item.class);
        final long itemId = entity.getId();
        entity.setAvailable(false);
        entity.setOwner(owner);

        final String setName = "setName";
        final String setDescription = "setDescription";
        final boolean setAvailable = true;
        Item updateEntity = Item.builder()
                .id(itemId)
                .name(setName)
                .description(setDescription)
                .available(setAvailable)
                .owner(owner)
                .request(null).build();

        ItemSimpleDto requestDto = ItemMapper.INSTANCE.toSimpleDto(updateEntity);

        // when
        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(entity));

        when(itemRepository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        when(itemRepository.save(any(Item.class)))
                .thenReturn(updateEntity);

        ItemDto response = itemService.update(userId, itemId, requestDto);

        // then
        assertEquals(setName, response.getName());
        assertEquals(setDescription, response.getDescription());
        assertEquals(setAvailable, requestDto.getAvailable());
        assertNotEquals(entity.getName(), response.getName());
        assertNotEquals(entity.getDescription(), response.getDescription());
        assertNotEquals(entity.isAvailable(), response.getAvailable());

        assertNotNull(response.getName());
        assertNotNull(response.getDescription());
        assertNotNull(response.getAvailable());

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);
        verify(itemRepository, times(1))
                .findById(itemId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyLong());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    @DisplayName("Update Name + Description, not Available")
    void update_whenEditNameDescription_thenReturnDto() {
        // given
        Item entity = items.get(0);
        final long userId = 1;
        final long itemId = entity.getId();

        final String setName = "setName";
        final String setDescription = "setDescription";

        ItemSimpleDto requestDto = ItemMapper.INSTANCE.toSimpleDto(entity);

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
                        anyLong());
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
                .existsById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyLong());
        verify(itemRepository, times(1))
                .updateNameAndDescriptionById(anyString(), anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Name + Available , not Description")
    void update_whenEditNameAvailable_thenReturnItemDto() {
        // given
        Item entity = itemStorage.get(1L);
        final long userId = 1;
        final long itemId = entity.getId();

        final String setName = "setName";
        final String setDescription = "";
        final Boolean setAvailable = !entity.isAvailable();

        ItemSimpleDto requestDto = ItemMapper.INSTANCE.toSimpleDto(entity);

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
                        anyLong());
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
                .existsById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyLong());
        verify(itemRepository, times(1))
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Name , not Description + Available")
    void update_whenEditName_thenReturnItemDto() {
        // given
        Item entity = itemStorage.get(1L);
        final long userId = 1;
        final long itemId = entity.getId();
        final String setName = "setName";
        final String setDescription = "";

        ItemSimpleDto requestDto = ItemMapper.INSTANCE.toSimpleDto(entity);
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
                        anyLong());
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
                .existsById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, times(1))
                .updateNameById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Description + Available, not Name")
    void update_whenEditDescriptionAvailable_thenReturnDto() {
        // given
        Item entity = itemStorage.get(1L);
        final long userId = 1;
        final long itemId = entity.getId();

        final String setName = "";
        final String setDescription = "setDescription";
        final Boolean setAvailable = !entity.isAvailable();

        ItemSimpleDto requestDto = ItemMapper.INSTANCE.toSimpleDto(entity);

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
                        anyLong());
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
                .existsById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, times(1))
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Description, not Name + Available")
    void update_whenEditDescription_thenReturnDto() {
        // given
        Item entity = itemStorage.get(1L);
        final long userId = 1;
        final long itemId = entity.getId();

        final String setName = "";
        final String setDescription = "setDescription";

        ItemSimpleDto requestDto = ItemMapper.INSTANCE.toSimpleDto(entity);

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
                        anyLong());
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
                .existsById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, times(1))
                .updateDescriptionById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateAvailableById(anyBoolean(), anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Update Available, not Name + Description")
    void update_whenEditAvailable_thenReturnDto() {
        // given
        Item entity = itemStorage.get(1L);
        final long userId = 1;
        final long itemId = entity.getId();

        final String setName = "";
        final String setDescription = "";
        final Boolean setAvailable = !entity.isAvailable();

        ItemSimpleDto requestDto = ItemMapper.INSTANCE.toSimpleDto(entity);

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
                        anyLong());
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
                .existsById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(itemRepository, never())
                .updateNameById(anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndDescriptionById(anyString(), anyString(), anyLong());
        verify(itemRepository, never())
                .updateNameAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionAndAvailableById(anyString(), anyBoolean(), anyLong());
        verify(itemRepository, never())
                .updateDescriptionById(anyString(), anyLong());
        verify(itemRepository, times(1))
                .updateAvailableById(anyBoolean(), anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }
}