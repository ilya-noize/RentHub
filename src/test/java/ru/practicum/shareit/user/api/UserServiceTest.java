package ru.practicum.shareit.user.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.InjectResources;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserMapper;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends InjectResources {

    @InjectMocks
    private UserServiceImpl service;
    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;

    @Test
    void create_whenSendValidUserDto_thenReturnUserDto() {

        when(mapper.toEntity(userDtoRequest))
                .thenReturn(userRequest);

        when(repository.save(userRequest))
                .thenReturn(userResponse);

        when(mapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        final UserDto userDto = service.create(userDtoRequest);

        assertEquals(userDtoRequest.getEmail(), userDto.getEmail());
        assertEquals(userDtoRequest.getName(), userDto.getName());
        assertNotNull(userDto.getId());

        verify(mapper, Mockito.times(1))
                .toEntity(userDtoRequest);
        verify(repository, Mockito.times(1))
                .save(userRequest);
        verify(mapper, Mockito.times(1))
                .toDto(userResponse);
    }

    @Test
    void get_whenGetWithId_thenReturnDto() {
        when(repository.existsById(anyInt()))
                .thenReturn(true);
        when(repository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(userResponse));
        when(mapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        service.get(1);

        verify(repository, times(1))
                .existsById(anyInt());
        verify(mapper, Mockito.times(1))
                .toDto(userResponse);
        verify(repository, times(1))
                .findById(anyInt());
    }

    @Test
    void get_whenGetWithNotExistId_thenReturnThrowException() {
        final int userId = 100;
        when(repository.existsById(userId))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.get(userId),
                "User with id:(" + userId + ") not exist");

        verify(repository, times(1))
                .existsById(anyInt());
        verify(mapper, never())
                .toDto(userResponse);
        verify(repository, never())
                .findById(anyInt());
    }

    @Test
    void getAll_whenGetAll_thenReturnDtoList() {

        when(repository.findAll()).thenReturn(users);

        List<UserDto> getAllUserDto = service.getAll();

        assertFalse(getAllUserDto.isEmpty());
        assertEquals(4, getAllUserDto.size());
    }

    @Test
    void delete_whenDeleteByExistId_thenOk() {
        /*
        when(repository.existsById(anyInt()))
                .thenReturn(true);

        doNothing().when(repository).deleteById(anyInt());
        */
    }

    @Test
    void delete_whenDeleteByNotExistId_thenThrowException() {
        when(repository.existsById(100))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.delete(100),
                "User with id:(100) not exist");

        verify(repository, times(0))
                .deleteById(1);
    }

    @Test
    void update_whenAllDataNotNull_thenReturnDto() {
        final String nameUpdate = "userUpdate";

        userDtoResponse.setName(nameUpdate);
        userResponse.setName(nameUpdate);
        when(repository.existsById(1))
                .thenReturn(true);
        when(repository.getReferenceById(1))
                .thenReturn(userRequest);
        when(mapper.toEntityFromDto(userDtoResponse))
                .thenReturn(userRequest);
        when(repository.save(userRequest))
                .thenReturn(userResponse);
        when(mapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        assertNotNull(userDtoRequest.getName());
        assertNotNull(userDtoRequest.getEmail());

        service.update(userDtoResponse);

        verify(repository, times(1))
                .existsById(1);
        verify(repository, times(1))
                .getReferenceById(1);
        verify(mapper, times(1))
                .toEntityFromDto(any(UserDto.class));
        verify(repository, times(1))
                .save(userRequest);
        verify(mapper, times(1))
                .toDto(any(User.class));
    }

    @Test
    void update_whenAllDataIsNull_thenReturnDto() {
        final String nameUpdate = "userUpdate";

        userDtoRequest.setName(null);
        userDtoRequest.setEmail(null);
        userDtoResponse.setName(nameUpdate);
        userResponse.setName(nameUpdate);
        when(repository.existsById(1))
                .thenReturn(true);
        when(repository.getReferenceById(1))
                .thenReturn(userRequest);
        when(mapper.toEntityFromDto(userDtoResponse))
                .thenReturn(userRequest);
        when(repository.save(userRequest))
                .thenReturn(userResponse);
        when(mapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        assertNull(userDtoRequest.getName());
        assertNull(userDtoRequest.getEmail());

        service.update(userDtoResponse);

        verify(repository, times(1))
                .existsById(1);
        verify(repository, times(1))
                .getReferenceById(1);
        verify(mapper, times(1))
                .toEntityFromDto(any(UserDto.class));
        verify(mapper, times(1))
                .toDto(any(User.class));
    }

    @Test
    void update_whenAllDataIsBlank_thenReturnDto() {
        final String nameUpdate = "userUpdate";

        userDtoRequest.setName("");
        userDtoRequest.setEmail("");
        userDtoResponse.setName(nameUpdate);
        userResponse.setName(nameUpdate);
        when(repository.existsById(1))
                .thenReturn(true);
        when(repository.getReferenceById(1))
                .thenReturn(userRequest);
        when(mapper.toEntityFromDto(userDtoResponse))
                .thenReturn(userRequest);
        when(repository.save(userRequest))
                .thenReturn(userResponse);
        when(mapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        assertTrue(userDtoRequest.getName().isBlank());
        assertTrue(userDtoRequest.getEmail().isBlank());

        service.update(userDtoResponse);

        verify(repository, times(1))
                .existsById(1);
        verify(repository, times(1))
                .getReferenceById(1);
        verify(mapper, times(1))
                .toEntityFromDto(any(UserDto.class));
        verify(mapper, times(1))
                .toDto(any(User.class));
    }
}