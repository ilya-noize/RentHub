package ru.practicum.shareit.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 * <p>
 * Booking class:
 * <p>
 * {@code id} Booking ID <br/>
 * {@code start} Start of booking <br/>
 * {@code end} End of booking <br/>
 * {@code item} Item <br/>
 * {@code booker} User <br/>
 * {@code status} Booking status
 */
@Data
@AllArgsConstructor
public class Booking {
    private final Long id;
    private final LocalDate start;
    private final LocalDate end;
    private final Long item;
    private final Long booker;
    private final Status status;
}
