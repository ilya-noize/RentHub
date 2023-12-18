package ru.practicum.shareit.booking.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.api.client.BookingClient;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.HEADER_USER_ID;
import static ru.practicum.shareit.constants.Constants.SIZE;

/**
 * <h3>Booking Controller</h3>
 * {@link #createBooking} Создание бронирования <br/>
 * {@link #updateStatusBooking} Изменить статус бронирования <br/>
 * {@link #getBooking}    Посмотреть бронирование <br/>
 * {@link #getAllBookingsForUser}  Посмотреть бронирования от имени пользователя <br/>
 * {@link #getAllBookingsForOwner} Посмотреть бронирования от имени владельца предмета <br/>
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final String createBooking = "/bookings";
    private final String updateStatusBooking = "/bookings/{id}";
    private final String getBooking = "/bookings/{id}";
    private final String getAllBookingsForUser = "/bookings";
    private final String getAllBookingsForOwner = "/bookings/owner";
    private final BookingClient bookingClient;

    @PostMapping(createBooking)
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody BookingSimpleDto bookingSimpleDto) {
        log.debug("POST {} - userId:{} - DTO:{}", createBooking, userId, bookingSimpleDto);

        return bookingClient.create(userId, bookingSimpleDto);
    }

    @PatchMapping(updateStatusBooking)
    public ResponseEntity<Object> update(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long id,
            @RequestParam boolean approved) {
        log.debug("PATCH {} - userId:{} - bookingId:{} - approved:{}",
                updateStatusBooking, userId, id, approved);

        return bookingClient.update(userId, id, approved);
    }

    @GetMapping(getBooking)
    public ResponseEntity<Object> get(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long id) {
        log.debug("GET {} - userId:{} - bookingId:{}", getBooking, userId, id);

        return bookingClient.getById(userId, id);
    }

    @GetMapping(getAllBookingsForUser)
    public ResponseEntity<Object> getAllByUser(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {
        log.debug("GET {} - userId:{} - state:{} - from:{} - size:{}",
                getAllBookingsForUser, userId, state, from, size);

        return bookingClient.getAllByUser(userId, state, from, size);
    }

    @GetMapping(getAllBookingsForOwner)
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {
        log.debug("GET {} - userId:{} - state:{} - from:{} - size:{}",
                getAllBookingsForOwner, userId, state, from, size);

        return bookingClient.getAllByOwner(userId, state, from, size);
    }
}