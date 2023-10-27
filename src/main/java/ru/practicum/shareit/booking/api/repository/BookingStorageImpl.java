package ru.practicum.shareit.booking.api.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.entity.Booking;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BookingStorageImpl implements BookingStorage {
    private final Map<Integer, Booking> bookings;

    @Override
    public Booking create(Booking booking) {
        return null;
    }

    @Override
    public Booking get(Integer id) {
        return null;
    }

    @Override
    public List<Booking> getAll() {
        return null;
    }

    @Override
    public Booking update(Integer id, Booking booking) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public boolean isExist(Integer id) {
        return false;
    }
}
