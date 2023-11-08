package ru.practicum.shareit.booking.api;

import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.entity.BookingFilterByTemplate;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingDto dto);

    BookingDto update(Integer id, Integer userId, Boolean approved);

    BookingDto get(Integer id, Integer userId);

    List<BookingDto> getAllByUser(BookingFilterByTemplate state, Integer userId);

    List<BookingDto> getAllByOwner(BookingFilterByTemplate state, Integer userId);
}
