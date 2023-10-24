package ru.practicum.shareit.user.api.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.api.Storage;
import ru.practicum.shareit.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserStorage implements Storage<User> {
    private final Map<Integer, User> users;
    Integer id = 0;

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

    @Override
    public User get(Integer id) {
        User user = users.get(id);
        log.debug("[i] GET Id:{} User:{}", id, user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(Integer id, User user) {
        users.replace(id, user);
        return get(id);
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
    }

    @Override
    public boolean isExist(Integer id) {
        return users.containsKey(id);
    }
}
