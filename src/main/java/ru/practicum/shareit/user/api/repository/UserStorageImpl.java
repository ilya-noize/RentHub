package ru.practicum.shareit.user.api.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users;
    Integer id = 0;

    /**
     * Создание пользователя
     * @param user  Новый пользователь
     * @return      Новый пользователь
     */
    @Override
    public User create(User user) {
        id++;
        user = User.builder()
                .id(id)
                .email(user.getEmail())
                .name(user.getName())
                .build();
        users.put(id, user);
        return get(id);
    }

    /**
     * @param id Идентификатор пользователя
     * @return   Пользователь
     */
    @Override
    public User get(Integer id) {
        User user = users.get(id);
        log.debug("[i] GET Id:{} User:{}", id, user);
        return user;
    }

    /**
     * Получение списка пользователей
     * @return      Список пользователей
     */
    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Обновление данных пользователя
     * @param id    Идентификатор пользователя
     * @param user  Обновлённые данные пользователя
     * @return      Обновлённый пользователь
     */
    @Override
    public User update(Integer id, User user) {
        users.replace(id, user);
        return get(id);
    }

    /**
     * Удаление пользователя
     * @param id    Идентификатор пользователя
     */
    @Override
    public void delete(Integer id) {
        users.remove(id);
    }

    /**
     * Существует ли пользователь?
     * @param id    Идентификатор пользователя
     * @return      true = YES, false = NO
     */
    @Override
    public boolean isExist(Integer id) {
        return users.containsKey(id);
    }
}
