package ru.practicum.shareit.item.api;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.api.dto.ItemDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ItemServiceUpdateTest extends ItemServiceMasterTest {

    @Test
        // N + D
    void update_whenNameAndDescriptionNotNull_thenReturnItemDto() {
        final int userId = 1;
        final int itemId = 1;

        String name = itemRequest.getName();
        String description = itemRequest.getDescription();
        //Boolean available = null;

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(repository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(repository.notExistsByIdAndOwner_Id(itemId, userId))
                .thenReturn(false);

        doNothing().when(repository).updateNameAndDescriptionById(name, description, itemId);

        when(mapper.toDto(itemRequest))
                .thenReturn(itemDtoResponse);

        assertEquals(itemDtoResponse, service.update(userId, itemId, itemDtoRequest));

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(repository, times(1))
                .findById(anyInt());
        verify(repository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(repository, never())
                .updateNameById(name, itemId);
        verify(repository, times(1))
                .updateNameAndDescriptionById(name, description, itemId);
        verify(repository, never())
                .updateNameAndAvailableById(name, false, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById(description, false, itemId);
        verify(repository, never())
                .updateDescriptionById(description, itemId);
        verify(repository, never())
                .updateAvailableById(false, itemId);
        verify(repository, never())
                .save(itemRequest);
    }

    @Test
        // N + A
    void update_whenNameAndAvailableNotNull_thenReturnItemDto() {
        final int userId = 1;
        final int itemId = 1;

        String name = itemRequest.getName();
        String description = null;
        boolean available = itemRequest.isAvailable();

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(repository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));

        doNothing().when(repository).updateNameAndAvailableById(name, available, itemId);

        when(mapper.toDto(itemResponse))
                .thenReturn(itemDtoResponse);

        ItemDto result = service.update(userId, itemId, itemDtoRequest);
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(repository, times(1))
                .findById(anyInt());
        verify(repository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(repository, never())
                .updateNameById(name, itemId);
        verify(repository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(repository, times(1))
                .updateNameAndAvailableById(name, available, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(repository, never())
                .updateDescriptionById(description, itemId);
        verify(repository, never())
                .updateAvailableById(available, itemId);
        verify(repository, never())
                .save(itemRequest);

    }

    @Test
        // N
    void update_whenNameNotNull_thenReturnItemDto() {
        final int userId = 1;
        final int itemId = 1;

        String name = itemRequest.getName();
        //String description = null;
        //Boolean available = null;

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(repository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));

        doNothing().when(repository).updateNameById(name, itemId);

        when(mapper.toDto(itemResponse))
                .thenReturn(itemDtoResponse);

        ItemDto result = service.update(userId, itemId, itemDtoRequest);
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(repository, times(1))
                .findById(anyInt());
        verify(repository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(repository, times(1))
                .updateNameById(name, itemId);
        verify(repository, never())
                .updateNameAndDescriptionById(name, "description", itemId);
        verify(repository, never())
                .updateNameAndAvailableById(name, false, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById("description", false, itemId);
        verify(repository, never())
                .updateDescriptionById("description", itemId);
        verify(repository, never())
                .updateAvailableById(false, itemId);
        verify(repository, never())
                .save(itemRequest);

    }

    @Test
        // D + A
    void update_whenDescriptionAndAvailableNotNull_thenReturnItemDto() {
        final int userId = 1;
        final int itemId = 1;

        String name = null;
        String description = itemRequest.getDescription();
        boolean available = itemRequest.isAvailable();

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(repository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));

        doNothing().when(repository).updateDescriptionAndAvailableById(description, available, itemId);

        when(mapper.toDto(itemResponse))
                .thenReturn(itemDtoResponse);

        ItemDto result = service.update(userId, itemId, itemDtoRequest);
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(repository, times(1))
                .findById(anyInt());
        verify(repository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(repository, never())
                .updateNameById(name, itemId);
        verify(repository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(repository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(repository, times(1))
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(repository, never())
                .updateDescriptionById(description, itemId);
        verify(repository, never())
                .updateAvailableById(available, itemId);
        verify(repository, never())
                .save(itemRequest);
    }

    @Test
        // D
    void update_whenDescriptionNotNull_thenReturnItemDto() {
        final int userId = 1;
        final int itemId = 1;

        //String name = null;
        String description = itemRequest.getDescription();
        //Boolean available = null;

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(repository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));

        doNothing().when(repository).updateDescriptionById(description, itemId);

        when(mapper.toDto(itemResponse))
                .thenReturn(itemDtoResponse);

        ItemDto result = service.update(userId, itemId, itemDtoRequest);
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(repository, times(1))
                .findById(anyInt());
        verify(repository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(repository, never())
                .updateNameById("name", itemId);
        verify(repository, never())
                .updateNameAndDescriptionById("name", description, itemId);
        verify(repository, never())
                .updateNameAndAvailableById("name", false, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById(description, false, itemId);
        verify(repository, times(1))
                .updateDescriptionById(description, itemId);
        verify(repository, never())
                .updateAvailableById(false, itemId);
        verify(repository, never())
                .save(itemRequest);
    }

    @Test
        // A
    void update_whenAvailableNotNull_thenReturnItemDto() {
        final int userId = 1;
        final int itemId = 1;

        String name = null;
        String description = null;
        boolean available = itemRequest.isAvailable();

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(repository.findById(itemId))
                .thenReturn(Optional.ofNullable(itemRequest));


//        doNothing().when(repository).updateNameById(name, itemId);
//        doNothing().when(repository).updateNameAndDescriptionById(name, description, itemId);
//        doNothing().when(repository).updateNameAndAvailableById(name, available, itemId);
//        doNothing().when(repository).updateDescriptionById(description, itemId);
//        doNothing().when(repository).updateDescriptionAndAvailableById(description, available, itemId);
        doNothing().when(repository).updateAvailableById(available, itemId);
//        when(repository.save(itemRequest))
//                .thenReturn(itemResponse);
        when(mapper.toDto(itemResponse))
                .thenReturn(itemDtoResponse);

        ItemDto result = service.update(userId, itemId, itemDtoRequest);
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getAvailable());

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(repository, times(1))
                .findById(anyInt());
        verify(repository, times(1))
                .notExistsByIdAndOwner_Id(itemId, userId);

        verify(repository, never())
                .updateNameById(name, itemId);
        verify(repository, never())
                .updateNameAndDescriptionById(name, description, itemId);
        verify(repository, never())
                .updateNameAndAvailableById(name, available, itemId);
        verify(repository, never())
                .updateDescriptionAndAvailableById(description, available, itemId);
        verify(repository, never())
                .updateDescriptionById(description, itemId);
        verify(repository, times(1))
                .updateAvailableById(available, itemId);
        verify(repository, never())
                .save(itemRequest);
    }

// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
//    @Test
//    void update_TEMPLATE() {
//        final int userId = 1;
//        final int itemId = 1;
//
//        String name = itemRequest.getName();
//        String description = itemRequest.getDescription();
//        boolean available = itemRequest.isAvailable();
//
//        when(userRepository.existsById(userId))
//                .thenReturn(true);
//
//        when(repository.findById(itemId))
//                .thenReturn(Optional.ofNullable(itemRequest));
//
////        doNothing().when(repository).updateNameById(name, itemId);
////        doNothing().when(repository).updateNameAndDescriptionById(name, description, itemId);
////        doNothing().when(repository).updateNameAndAvailableById(name, available, itemId);
////        doNothing().when(repository).updateDescriptionById(description, itemId);
////        doNothing().when(repository).updateDescriptionAndAvailableById(description, available, itemId);
////        doNothing().when(repository).updateAvailableById(available, itemId);
////        when(repository.save(itemRequest))
////                .thenReturn(itemResponse);
//        when(mapper.toDto(itemResponse))
//                .thenReturn(itemDtoResponse);
//
//        ItemDto result = service.update(userId, itemId, itemDtoRequest);
//        assertNotNull(result.getName());
//        assertNotNull(result.getDescription());
//        assertNotNull(result.getAvailable());
//
//        verify(userRepository, times(1))
//                .existsById(anyInt());
//        verify(repository, times(1))
//                .findById(anyInt());
//        verify(repository, times(1))
//                .notExistsByIdAndOwner_Id(itemId, userId);
//
//        verify(repository, never())
//                .updateNameById(name, itemId);
//        verify(repository, never())
//                .updateNameAndDescriptionById(name, description, itemId);
//        verify(repository, never())
//                .updateNameAndAvailableById(name, available, itemId);
//        verify(repository, never())
//                .updateDescriptionAndAvailableById(description, available, itemId);
//        verify(repository, never())
//                .updateDescriptionById(description, itemId);
//        verify(repository, never())
//                .updateAvailableById(available, itemId);
//        verify(repository, never())
//                .save(itemRequest);
//    }
}