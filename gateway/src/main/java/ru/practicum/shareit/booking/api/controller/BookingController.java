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
import ru.practicum.shareit.booking.api.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final String FROM = "0";
    private final String SIZE = "10";

    private final String CREATE_BOOKING = "/bookings";
    private final String UPDATE_STATUS_BOOKING = "/bookings/{id}";
    private final String GET_BOOKING = "/bookings/{id}";
    private final String GET_ALL_BOOKINGS_FOR_USER = "/bookings";
    private final String GET_ALL_BOOKINGS_FOR_OWNER = "/bookings/owner";
    private final BookingClient bookingClient;

    @PostMapping(CREATE_BOOKING)
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @Valid @RequestBody BookingSimpleDto bookingSimpleDto) {

        return bookingClient.create(userId, bookingSimpleDto);
    }

    @PatchMapping(UPDATE_STATUS_BOOKING)
    public ResponseEntity<Object> update(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {

        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping(GET_BOOKING)
    public ResponseEntity<Object> get(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Long id) {

        return bookingClient.getById(userId, id);
    }

    @GetMapping(GET_ALL_BOOKINGS_FOR_USER)
    public ResponseEntity<Object> getAllByUser(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {

        return bookingClient.getAllByUser(userId, state, from, size);
    }

    @GetMapping(GET_ALL_BOOKINGS_FOR_OWNER)
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {

        return bookingClient.getAllByOwner(userId, state, from, size);
    }
}