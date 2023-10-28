package ru.practicum.shareit.api;

import java.util.List;

public interface CRUDService<T> {
    T create(T t);

    T get(Integer id);

    List<T> getAll();

    T update(Integer id, T t);

    void delete(Integer id);
}
