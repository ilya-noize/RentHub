package ru.practicum.shareit.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Just a class with data. Don't touch him.
 */
@Data
@Builder
@AllArgsConstructor
public final class BookingSimpleDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
