package ru.practicum.shareit.api;


public interface Mapper<E, D> {
    D makeEntityToDto(E entity);

    E makeDtoToEntity(D dto);
}
