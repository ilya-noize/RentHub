package ru.practicum.shareit.user.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserMapper;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.constants.Constants.RANDOM;
import static ru.practicum.shareit.constants.Constants.USER_NOT_EXISTS;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final User user = RANDOM.nextObject(User.class);
    private UserSimpleDto requestDto;

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

        requestDto = new UserSimpleDto(user.getEmail(), user.getName());
    }

    @Test
    void create() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        final UserDto userDto = userService.create(requestDto);

        assertEquals(requestDto.getEmail(), userDto.getEmail());
        assertEquals(requestDto.getName(), userDto.getName());
        assertNotNull(userDto.getId());

        verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void get_ok() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        userService.get(1);

        verify(userRepository, times(1))
                .findById(anyInt());
    }

    @Test
    void get_404() {
        int userId = 1;
        when(userRepository.findById(userId))
                .thenThrow(new NotFoundException(format(USER_NOT_EXISTS, userId)));

        assertThrows(NotFoundException.class,
                () -> userService.get(userId),
                format(USER_NOT_EXISTS, userId));

        verify(userRepository, times(1))
                .findById(userId);
    }

    @Test
    void getAll() {

        List<User> expected = List.of(user);
        when(userRepository.findAll()).thenReturn(expected);

        List<UserDto> response = userService.getAll();

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
    }

    @Test
    void delete() {
        int id = user.getId();

        when(userRepository.existsById(id)).thenReturn(true);

        doNothing().when(userRepository).deleteById(id);

        userService.delete(id);

        verify(userRepository, times(1))
                .deleteById(id);
    }

    @Test
    void delete_notExists_Throw() {
        int id = user.getId();

        when(userRepository.existsById(id))
                .thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.delete(id),
                format(USER_NOT_EXISTS, id));

        assertEquals(e.getMessage(),
                format(USER_NOT_EXISTS, id));

        verify(userRepository, never())
                .deleteById(id);
    }

    @Test
    void update_notFound_throw() {
        int userId = user.getId();
        UserDto updateDto = UserMapper.INSTANTS.toDto(user);

        when(userRepository.findById(anyInt()))
                .thenThrow(new NotFoundException(format(USER_NOT_EXISTS, userId)));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.update(updateDto), format(USER_NOT_EXISTS, userId));
        assertEquals(e.getMessage(), format(USER_NOT_EXISTS, userId));
    }

    @Test
    void update() {
        int userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName("King");
        updateUser.setEmail("vi@king.com");
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(updateDto, actual);
        assertNotEquals(user.getEmail(), actual.getEmail());
        assertNotEquals(user.getName(), actual.getName());
    }

    @Test
    void update_onlyName_emailNull() {
        int userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName("King");
        updateUser.setEmail(user.getEmail());
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);
        updateDto.setEmail(null);

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(updateUser.getName(), actual.getName());
        assertEquals(user.getEmail(), actual.getEmail());
    }

    @Test
    void update_onlyName_emailBlank() {
        int userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName("King");
        updateUser.setEmail(user.getEmail());
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);
        updateDto.setEmail("");

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(updateUser.getName(), actual.getName());
        assertEquals(user.getEmail(), actual.getEmail());
    }

    @Test
    void update_onlyEmail_nameNull() {
        int userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName(user.getName());
        updateUser.setEmail("vi@king.com");
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);
        updateDto.setName(null);

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(user.getName(), actual.getName());
        assertEquals(updateUser.getEmail(), actual.getEmail());
    }


    @Test
    void update_onlyEmail_nameBlank() {
        int userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName(user.getName());
        updateUser.setEmail("vi@king.com");
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);
        updateDto.setName("");

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(user.getName(), actual.getName());
        assertEquals(updateUser.getEmail(), actual.getEmail());
    }
}
