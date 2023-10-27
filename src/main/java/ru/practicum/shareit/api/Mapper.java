package ru.practicum.shareit.api;

public interface Mapper<E, D> {
    D toDto(E e);
    E toEntity(D d);
}
