package ru.practicum.shareit.api;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CRUDService<T> {
    T create(T dto);

    T get(Integer id);

    List<T> getAll();

    T update(Integer id, T dto);

    void delete(Integer id);
}
