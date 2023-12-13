package ru.practicum.shareit.user.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserMapper;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.practicum.shareit.constants.Constants.USER_NOT_EXISTS;

/**
 * DTO processing class before saving to memory
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserSimpleDto userDto) {
        log.debug("[d] Create user {}", userDto);
        User user = UserMapper.INSTANTS.toEntity(userDto);

        return UserMapper.INSTANTS.toDto(userRepository.save(user));
    }

    /**
     * Gets the user by ID
     * <p>
     *
     * @param id User ID
     * @return User
     * @see #isExist(Integer)
     */
    @Override
    public UserDto get(Integer id) {
        log.debug("[i] get User by ID:{}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        format(USER_NOT_EXISTS, id)));

        return UserMapper.INSTANTS.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("[i] get All Users");

        return userRepository.findAll()
                .stream()
                .map(UserMapper.INSTANTS::toDto)
                .collect(Collectors.toList());
    }

    /**
     * <p>Обновление пользователя</p>
     * <ul>Проверка на уникальность почты игнорируется, если:
     *      <li> почта не указана (значение берётся из репозитория) </li>
     *      <li> почта указана и её значение совпадает со значением из репозитория </li>
     * </ul>
     *
     * @param userDto UserDTO from Controller
     * @return UserDTO for Controller
     */
    @Override
    public UserDto update(UserDto userDto) {
        int userId = userDto.getId();
        log.debug("[i] update User:{} by ID:{}", userDto, userId);
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        format(USER_NOT_EXISTS, userId)));

        String name = userDto.getName();
        boolean nameIsNullOrBlank = name == null || name.isBlank();
        if (nameIsNullOrBlank) {
            userDto.setName(userEntity.getName());
        }

        String email = userDto.getEmail();
        boolean emailIsNullOrBlank = email == null || email.isBlank();
        if (emailIsNullOrBlank) {
            userDto.setEmail(userEntity.getEmail());
        }

        return UserMapper.INSTANTS.toDto(
                userRepository.save(
                        UserMapper.INSTANTS.toEntityFromDto(userDto)));
    }

    @Override
    public void delete(Integer id) {
        log.debug("[i] delete User by ID:{}", id);
        isExist(id);
        userRepository.deleteById(id);
    }

    private void isExist(Integer id) {
        log.debug("[i] is exist User by ID:{}", id);
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(
                    String.format(USER_NOT_EXISTS, id));
        }
    }
}
