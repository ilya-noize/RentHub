package ru.practicum.shareit.user.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.api.CRUDRepository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserMapper;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO processing class before saving to memory
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CRUDRepository<User> repository;
    private final UserMapper mapper;

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("[d] Create user {}", userDto);
        if (userDto.getEmail() == null) {
            throw new NullPointerException("Email пользователя не может быть пустым");
        }
        if (userDto.getName() == null) {
            userDto.setName("");
        }
        User user = mapper.toEntity(userDto);
        checkUniqueEmail(user.getEmail());

        return mapper.toDto(repository.create(user));
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
        isExist(id);
        User user = repository.get(id);
        return mapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("[i] get All Users");
        return repository.getAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Integer id, UserDto userDto) {
        log.debug("[i] update User:{} by ID:{}", userDto, id);
        isExist(id);
        boolean checkEmail = true;

        userDto.setId(id);
        if (userDto.getName() == null) {
            userDto.setName(get(id).getName());
        }

        String email = get(id).getEmail();
        if (userDto.getEmail() == null) {
            userDto.setEmail(email);
            checkEmail = false;
        } else {
            if (userDto.getEmail().equals(email)) {
                checkEmail = false;
            }
        }
        User user = mapper.toEntity(userDto);

        if (checkEmail) {
            checkUniqueEmail(user.getEmail());
        }

        user = repository.update(id, user);
        return mapper.toDto(user);
    }

    @Override
    public void delete(Integer id) {
        log.debug("[i] delete User by ID:{}", id);
        isExist(id);
        repository.delete(id);
    }

    private void isExist(Integer id) {
        log.debug("[i] is exist User by ID:{}", id);
        if (!repository.isExist(id)) {
            throw new NotFoundException(String.format("user.id(%d) is not exist!", id));
        }
    }

    /**
     * Checking email for uniqueness
     * <p>
     *
     * @param email User
     */
    private void checkUniqueEmail(String email) {
        log.debug("[i] check unique email:{} by User", email);
        boolean isNotUnique = repository.getAll().stream()
                .map(User::getEmail)
                .anyMatch(eMail -> eMail.equals(email));
        if (isNotUnique) {
            throw new AlreadyExistsException("This email is already exists.");
        }
    }
}
