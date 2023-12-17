package ru.practicum.shareit.valid;

import ru.practicum.shareit.exception.BadRequestException;

public interface ValidPageable {
    static void check(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Pageable incorrect");
        }
    }
}
