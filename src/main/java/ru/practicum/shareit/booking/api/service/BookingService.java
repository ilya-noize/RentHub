package ru.practicum.shareit.booking.api.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto create(Integer bookerId, BookingSimpleDto dto);

    BookingDto update(Integer ownerId, Long bookingId, Boolean approved);

    BookingDto get(Integer userId, Long bookingId);

    List<BookingDto> getAllByUser(Integer bookerId, String state, LocalDateTime now, Pageable pageable);

    List<BookingDto> getAllByOwner(Integer ownerId, String state, LocalDateTime now, Pageable pageable);
}
