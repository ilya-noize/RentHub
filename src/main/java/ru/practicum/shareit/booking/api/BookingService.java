package ru.practicum.shareit.booking.api;

import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingDtoRecord;

import java.util.List;

public interface BookingService {

    BookingDtoRecord create(Integer userId, BookingDto dto);

    BookingDtoRecord update(Integer userId, Long bookingId, Boolean approved);

    BookingDtoRecord get(Integer userId, Long bookingId);

    List<BookingDtoRecord> getAllByUser(Integer userId, String state);

    List<BookingDtoRecord> getAllByOwner(Integer bookerId, String state);
}
