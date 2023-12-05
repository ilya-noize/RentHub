package ru.practicum.shareit.user.api.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Not worked
 * NPE when running userMapper;
 */

@SpringBootTest
class UserMapperTestIT extends InjectResources {

    @Autowired
    private UserMapper userMapper;


    @Test
    void givenUserToUserDto_whenMaps_thenCorrect() {
        User user = userStorage.get(1);

        UserDto response = userMapper.toDto(user);

        assertEquals(response.getId(), user.getId());
        assertEquals(response.getName(), user.getName());
        assertEquals(response.getEmail(), user.getEmail());
    }

    @Test
    void givenUserDtoToUser_whenMaps_thenCorrect() {
        User user = userStorage.get(1);

        UserDto request = userMapper.toDto(user);
        User response = userMapper.toEntityFromDto(request);

        assertEquals(response.getId(), request.getId());
        assertEquals(response.getName(), request.getName());
        assertEquals(response.getEmail(), request.getEmail());
    }

    @Test
    void givenUserSimpleDtoToUser_whenMaps_thenCorrect() {
        User user = userStorage.get(1);
        UserSimpleDto request = new UserSimpleDto(user.getEmail(), user.getName());

        User response = userMapper.toEntity(request);

        assertNull(response.getId());
        assertEquals(response.getEmail(), request.getEmail());
        assertEquals(response.getName(), request.getName());
    }


    @Test
    void toEntityFromDto_whenMaps_thenThrow() {
        User user = null;

        UserDto request = userMapper.toDto(user);

        User response = userMapper.toEntityFromDto(request);

        assertNull(request);
        assertNull(response);
    }
}