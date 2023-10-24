package ru.practicum.shareit.user.api.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.api.Mapper;
import ru.practicum.shareit.user.entity.User;

@Component
public class UserMapper implements Mapper<User, UserDto> {
    public UserDto makeEntityToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName()).build();
    }

    @Override
    public User makeDtoToEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail()).build();
    }
}
