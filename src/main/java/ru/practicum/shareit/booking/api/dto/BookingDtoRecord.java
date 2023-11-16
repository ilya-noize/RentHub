package ru.practicum.shareit.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;

import java.time.LocalDateTime;

/**
 * Just a class with data. Don't touch him.
 */
@Data
@AllArgsConstructor
public final class BookingDtoRecord {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final BookingStatus status;
    private final BookerDto booker;
    private final ItemDto item;

    @Data
    static
    class ItemDto {
        private final Integer id;
        private final String name;
    }

    @Data
    static
    class BookerDto {
        private final Integer id;
    }
}
