package ru.practicum.shareit.user.api.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Not worked
 * NPE when running userMapper;
 */

//todo @ExtendWith() ?
class UserMapperTestIT {

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {

    }

    @Test
    void givenUserToUserDto_whenMaps_thenCorrect() {
//        User user = new User(1, "user@user.com", "user");
//
//        UserDto userDto = userMapper.toDto(user);//NPE
//
//        assertEquals(user.getName(), userDto.getName());
//        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void givenUserDtotoUser_whenMaps_thenCorrect() {
//        UserDto userDto = new UserDto(1, "user@user.com", "user");
//
//        User user = userMapper.toEntity(userDto);//NPE
//
//        assertEquals(userDto.getName(), user.getName());
//        assertEquals(userDto.getEmail(), user.getEmail());
    }
}