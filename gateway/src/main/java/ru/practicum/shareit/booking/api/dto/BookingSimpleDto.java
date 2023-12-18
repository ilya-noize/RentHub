package ru.practicum.shareit.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Just a class with data. Don't touch him.
 */
@Data
@Builder
@AllArgsConstructor
public final class BookingSimpleDto {
    @NotNull(message = "Start is null")
    @FutureOrPresent(message = "Start can't be not Future Or Present")
    private LocalDateTime start;

    @NotNull(message = "Finish is null")
    @Future(message = "Finish can't be not in Future")
    private LocalDateTime end;

    @NotNull(message = "ItemId is null")
    private Long itemId;
}
