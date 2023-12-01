package ru.practicum.shareit.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;

import java.time.LocalDateTime;

/**
 * Just a class with data. Don't touch him.
 */
@AllArgsConstructor
@Getter
public final class BookingDtoRecord {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final BookingStatus status;
    private final ItemDto item;
    private final BookerDto booker;

    @AllArgsConstructor
    @Getter
    static
    class ItemDto {
        private final Integer id;
        private final String name;
    }

    @AllArgsConstructor
    @Getter
    static
    class BookerDto {
        private final Integer id;
    }
}
