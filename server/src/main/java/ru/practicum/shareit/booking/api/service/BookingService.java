package ru.practicum.shareit.booking.api.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.entity.enums.BookingState;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto create(Long bookerId, BookingSimpleDto dto);

    BookingDto update(Long ownerId, Long bookingId, Boolean approved);

    BookingDto get(Long userId, Long bookingId);

    List<BookingDto> getAllByUser(Long bookerId, BookingState state, LocalDateTime now, Pageable pageable);

    List<BookingDto> getAllByOwner(Long ownerId, BookingState state, LocalDateTime now, Pageable pageable);
}
