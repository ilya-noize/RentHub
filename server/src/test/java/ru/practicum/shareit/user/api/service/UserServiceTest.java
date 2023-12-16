package ru.practicum.shareit.user.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.Mockito.anyLong;
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
    @Disabled
    void create() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        final UserDto userDto = userService.create(requestDto);

        assertEquals(requestDto.getEmail(), userDto.getEmail());
        assertEquals(requestDto.getName(), userDto.getName());
        assertNotNull(userDto.getId());

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    @Disabled
    void get_ok() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.get(1L);

        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void get_404() {
        long userId = 1;
        when(userRepository.findById(userId))
                .thenThrow(new NotFoundException(format(USER_NOT_EXISTS, userId)));

        assertThrows(NotFoundException.class,
                () -> userService.get(userId),
                format(USER_NOT_EXISTS, userId));

        verify(userRepository, times(1))
                .findById(userId);
    }

    @Test
    @Disabled
    void getAll() {

        List<User> expected = List.of(user);
        when(userRepository.findAll()).thenReturn(expected);

        List<UserDto> response = userService.getAll();

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
    }

    @Test
    void delete() {
        long id = user.getId();

        when(userRepository.existsById(id)).thenReturn(true);

        doNothing().when(userRepository).deleteById(id);

        userService.delete(id);

        verify(userRepository, times(1))
                .deleteById(id);
    }

    @Test
    void delete_notExists_Throw() {
        long id = user.getId();

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
    @Disabled
    void update_notFound_throw() {
        long userId = user.getId();
        UserDto updateDto = UserMapper.INSTANTS.toDto(user);

        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(format(USER_NOT_EXISTS, userId)));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.update(updateDto), format(USER_NOT_EXISTS, userId));
        assertEquals(e.getMessage(), format(USER_NOT_EXISTS, userId));
    }

    @Test
    @Disabled
    void update() {
        long userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName("King");
        updateUser.setEmail("vi@king.com");
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(updateDto, actual);
        assertNotEquals(user.getEmail(), actual.getEmail());
        assertNotEquals(user.getName(), actual.getName());
    }

    @Test
    @Disabled
    void update_onlyName_emailNull() {
        long userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName("King");
        updateUser.setEmail(user.getEmail());
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);
        updateDto.setEmail(null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(updateUser.getName(), actual.getName());
        assertEquals(user.getEmail(), actual.getEmail());
    }

    @Test
    @Disabled
    void update_onlyName_emailBlank() {
        long userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName("King");
        updateUser.setEmail(user.getEmail());
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);
        updateDto.setEmail("");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(updateUser.getName(), actual.getName());
        assertEquals(user.getEmail(), actual.getEmail());
    }

    @Test
    @Disabled
    void update_onlyEmail_nameNull() {
        long userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName(user.getName());
        updateUser.setEmail("vi@king.com");
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);
        updateDto.setName(null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(user.getName(), actual.getName());
        assertEquals(updateUser.getEmail(), actual.getEmail());
    }


    @Test
    @Disabled
    void update_onlyEmail_nameBlank() {
        long userId = user.getId();

        User updateUser = RANDOM.nextObject(User.class);
        updateUser.setId(userId);
        updateUser.setName(user.getName());
        updateUser.setEmail("vi@king.com");
        UserDto updateDto = UserMapper.INSTANTS.toDto(updateUser);
        updateDto.setName("");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        UserDto actual = userService.update(updateDto);
        assertEquals(user.getName(), actual.getName());
        assertEquals(updateUser.getEmail(), actual.getEmail());
    }
}
