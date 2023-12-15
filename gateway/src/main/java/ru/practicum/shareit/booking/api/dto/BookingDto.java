package ru.practicum.shareit.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.api.enums.BookingStatus;

import java.time.LocalDateTime;

/**
 * Just a class with data. Don't touch him.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private ItemDto item;
    private BookerDto booker;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static
    class ItemDto {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static
    class BookerDto {
        private Integer id;
    }
}
