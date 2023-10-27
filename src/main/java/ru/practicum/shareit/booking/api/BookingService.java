package ru.practicum.shareit.booking.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.repository.BookingStorageImpl;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingStorageImpl repository;
}
