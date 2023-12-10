package ru.practicum.shareit.user.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ShareItApp.USER_NOT_EXISTS;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends InjectResources {
    private final User user = random.nextObject(User.class);
    private User userResponse;
    private UserSimpleDto requestDto;

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userResponse = user;

        requestDto = new UserSimpleDto(user.getEmail(), user.getName());
    }

    @Test
    void create_whenSendValidUserDto_thenReturnUserDto() {
        when(userRepository.save(any(User.class)))
                .thenReturn(userResponse);

        final UserDto userDto = userService.create(requestDto);

        assertEquals(requestDto.getEmail(), userDto.getEmail());
        assertEquals(requestDto.getName(), userDto.getName());
        assertNotNull(userDto.getId());

        verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void get_whenGetWithId_thenReturnDto() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(userResponse));

        userService.get(1);

        verify(userRepository, times(1))
                .findById(anyInt());
    }

    @Test
    void getAll_whenGetAll_thenReturnDtoList() {

        List<User> expected = List.of(user);
        when(userRepository.findAll()).thenReturn(expected);

        List<UserDto> response = userService.getAll();

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
    }

    @Test
    void delete_whenDeleteByExistId_thenOk() {
        int id = user.getId();

        when(userRepository.existsById(id)).thenReturn(true);

        doNothing().when(userRepository).deleteById(id);

        userService.delete(id);

        verify(userRepository, times(1))
                .deleteById(id);
    }

    @Test
    void delete_whenDeleteByNotExistId_thenThrowException() {
        int id = user.getId();

        when(userRepository.existsById(id))
                .thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.delete(id));

        assertEquals(e.getMessage(),
                format(USER_NOT_EXISTS, id));

        verify(userRepository, never())
                .deleteById(id);
    }
}
