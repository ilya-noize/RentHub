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
import ru.practicum.shareit.user.api.dto.UserMapper;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ShareItApp.LOGGING_IS_NEEDED_IN_TEST;
import static ru.practicum.shareit.ShareItApp.USER_WITH_ID_NOT_EXIST;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends InjectResources {

    Map<Integer, User> userStorage;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void createsEnvironmentObjects() {

        if (users.isEmpty()) {
            throw new RuntimeException("No Users.");
        }

        if (LOGGING_IS_NEEDED_IN_TEST) {
            System.out.println("- ".repeat(40));
            for (User u : users) {
                System.out.printf("User:[%d]\t%s%n", u.getId(), u);
            }
        }

        userStorage = users.stream()
                .collect(toMap(User::getId,
                        Function.identity(),
                        (first, second) -> first));
    }

    @Test
    void create_whenSendValidUserDto_thenReturnUserDto() {

        when(userMapper.toEntity(userDtoRequest))
                .thenReturn(userRequest);

        when(userRepository.save(userRequest))
                .thenReturn(userResponse);

        when(userMapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        final UserDto userDto = userService.create(userDtoRequest);

        assertEquals(userDtoRequest.getEmail(), userDto.getEmail());
        assertEquals(userDtoRequest.getName(), userDto.getName());
        assertNotNull(userDto.getId());

        verify(userMapper, Mockito.times(1))
                .toEntity(userDtoRequest);
        verify(userRepository, Mockito.times(1))
                .save(userRequest);
        verify(userMapper, Mockito.times(1))
                .toDto(userResponse);
    }

    @Test
    void get_whenGetWithId_thenReturnDto() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(userResponse));
        when(userMapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        userService.get(1);

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(userMapper, Mockito.times(1))
                .toDto(userResponse);
        verify(userRepository, times(1))
                .findById(anyInt());
    }

    @Test
    void get_whenGetWithNotExistId_thenReturnThrowException() {
        final int id = userStorage.get(1).getId();
        when(userRepository.existsById(id))
                .thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.get(id));

        assertEquals(e.getMessage(), format(USER_WITH_ID_NOT_EXIST, id));

        verify(userRepository, times(1))
                .existsById(anyInt());
        verify(userMapper, never())
                .toDto(userResponse);
        verify(userRepository, never())
                .findById(anyInt());
    }

    @Test
    void getAll_whenGetAll_thenReturnDtoList() {

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> getAllUserDto = userService.getAll();

        assertFalse(getAllUserDto.isEmpty());
        assertEquals(4, getAllUserDto.size());
    }

    @Test
    void delete_whenDeleteByExistId_thenOk() {
        int id = userStorage.get(1).getId();

        when(userRepository.existsById(id)).thenReturn(true);

        doNothing().when(userRepository).deleteById(id);

        userService.delete(id);

        verify(userRepository, times(1))
                .deleteById(id);
    }

    @Test
    void delete_whenDeleteByNotExistId_thenThrowException() {
        int id = userStorage.get(1).getId();

        when(userRepository.existsById(id))
                .thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.delete(id));

        assertEquals(e.getMessage(),
                format(USER_WITH_ID_NOT_EXIST, id));

        verify(userRepository, never())
                .deleteById(id);
    }

    @Test
    void update_whenAllDataNotNull_thenReturnDto() {
        final String nameUpdate = "userUpdate";
        final String emailUpdate = "userUpdate@user.com";

        userDtoResponse.setName(nameUpdate);
        userResponse.setName(nameUpdate);
        userDtoResponse.setEmail(emailUpdate);
        userResponse.setEmail(emailUpdate);

        when(userRepository.existsById(1))
                .thenReturn(true);
        when(userRepository.getReferenceById(1))
                .thenReturn(userRequest);
        when(userMapper.toEntityFromDto(userDtoResponse))
                .thenReturn(userRequest);
        when(userRepository.save(userRequest))
                .thenReturn(userResponse);
        when(userMapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        assertNotNull(userDtoRequest.getName());
        assertNotNull(userDtoRequest.getEmail());

        userService.update(userDtoResponse);
        assertEquals(userDtoResponse.getName(), nameUpdate);
        assertEquals(userDtoResponse.getEmail(), emailUpdate);

        verify(userRepository, times(1))
                .existsById(1);
        verify(userRepository, times(1))
                .getReferenceById(1);
        verify(userMapper, times(1))
                .toEntityFromDto(any(UserDto.class));
        verify(userRepository, times(1))
                .save(userRequest);
        verify(userMapper, times(1))
                .toDto(any(User.class));
    }

    @Test
    void update_whenAllDataIsNull_thenReturnDto() {
        final String nameUpdate = "userUpdate";

        userDtoRequest.setName(null);
        userDtoRequest.setEmail(null);
        userDtoResponse.setName(nameUpdate);
        userResponse.setName(nameUpdate);

        when(userRepository.existsById(1))
                .thenReturn(true);
        when(userRepository.getReferenceById(1))
                .thenReturn(userRequest);
        when(userMapper.toEntityFromDto(userDtoResponse))
                .thenReturn(userRequest);
        when(userRepository.save(userRequest))
                .thenReturn(userResponse);
        when(userMapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        assertNull(userDtoRequest.getName());
        assertNull(userDtoRequest.getEmail());

        UserDto response = userService.update(userDtoResponse);

        assertEquals(response.getName(), nameUpdate);
        assertNotEquals(response.getEmail(), userDtoRequest.getEmail());

        verify(userRepository, times(1))
                .existsById(1);
        verify(userRepository, times(1))
                .getReferenceById(1);
        verify(userMapper, times(1))
                .toEntityFromDto(any(UserDto.class));
        verify(userMapper, times(1))
                .toDto(any(User.class));
    }

    @Test
    void update_whenAllDataIsBlank_thenReturnDto() {
        final String nameUpdate = "userUpdate";

        userDtoRequest.setName("");
        userDtoRequest.setEmail("");
        userDtoResponse.setName(nameUpdate);
        userResponse.setName(nameUpdate);
        when(userRepository.existsById(1))
                .thenReturn(true);
        when(userRepository.getReferenceById(1))
                .thenReturn(userRequest);
        when(userMapper.toEntityFromDto(userDtoResponse))
                .thenReturn(userRequest);
        when(userRepository.save(userRequest))
                .thenReturn(userResponse);
        when(userMapper.toDto(userResponse))
                .thenReturn(userDtoResponse);

        assertTrue(userDtoRequest.getName().isBlank());
        assertTrue(userDtoRequest.getEmail().isBlank());

        userService.update(userDtoResponse);

        verify(userRepository, times(1))
                .existsById(1);
        verify(userRepository, times(1))
                .getReferenceById(1);
        verify(userMapper, times(1))
                .toEntityFromDto(any(UserDto.class));
        verify(userMapper, times(1))
                .toDto(any(User.class));
    }
}