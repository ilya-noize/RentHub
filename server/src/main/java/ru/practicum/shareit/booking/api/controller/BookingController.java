package ru.practicum.shareit.booking.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.api.service.BookingService;
import ru.practicum.shareit.booking.entity.enums.BookingState;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.constants.Constants.CREATE_BOOKING;
import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.GET_ALL_BOOKINGS_FOR_OWNER;
import static ru.practicum.shareit.constants.Constants.GET_ALL_BOOKINGS_FOR_USER;
import static ru.practicum.shareit.constants.Constants.GET_BOOKING;
import static ru.practicum.shareit.constants.Constants.HEADER_USER_ID;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.UPDATE_STATUS_BOOKING;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    /**
     * Запрос может быть создан любым пользователем,<br/>
     * а затем подтверждён владельцем вещи.
     */
    @PostMapping(CREATE_BOOKING)
    public BookingDto create(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @RequestBody BookingSimpleDto dto) {
        log.debug("Point: [{}]\nIncoming: Dto:{} UserId:{}", CREATE_BOOKING, dto, userId);

        return service.create(userId, dto);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование.<br/>
     * Может быть выполнено только владельцем вещи.<br/>
     * Затем статус бронирования становится либо APPROVED, либо REJECTED.
     *
     * @param id       booking ID
     * @param userId   user ID - Owner
     * @param approved Booking status (true = APPROVED / false = REJECTED)
     */
    @PatchMapping(UPDATE_STATUS_BOOKING)
    public BookingDto update(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Long id,
            @RequestParam Boolean approved) {
        log.debug("[i] UPDATE_STATUS_BOOKING\n USER_ID:{}, BOOKING_ID:{}, APPROVED:{}",
                userId, id, approved);

        return service.update(userId, id, approved);
    }

    /**
     * Получение данных о конкретном бронировании (включая его статус).<br/>
     * Может быть выполнено либо автором бронирования,<br/>
     * либо владельцем вещи, к которой относится бронирование.
     *
     * @param userId User ID
     * @param id     Booking ID
     */
    @GetMapping(GET_BOOKING)
    public BookingDto get(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Long id) {
        log.debug("[i] GET_BOOKING\n USER_ID:{}, BOOKING_ID:{}",
                userId, id);

        return service.get(userId, id);
    }

    /**
     * Получение списка всех бронирований текущего пользователя.<br/>
     * Список предметов взятых в аренду когда-либо.
     *
     * @param bookerId User ID - Booker
     * @param state    Search filter
     */
    @GetMapping(GET_ALL_BOOKINGS_FOR_USER)
    public List<BookingDto> getAllByUser(
            @RequestHeader(HEADER_USER_ID) Integer bookerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(required = false, defaultValue = FROM) Integer from,
            @RequestParam(required = false, defaultValue = SIZE) Integer size) {
        log.debug("[i] GET_ALL_BOOKINGS_FOR_USER\n BOOKER_ID:{}, STATE:{}",
                bookerId, state);

        return service.getAllByUser(
                bookerId,
                state,
                LocalDateTime.now(),
                checkPageable(from, size));
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя.<br/>
     * Список предметов доступных для аренды от пользователя.
     *
     * @param ownerId User ID - Owner
     * @param state   Search filter
     */
    @GetMapping(GET_ALL_BOOKINGS_FOR_OWNER)
    public List<BookingDto> getAllByOwner(
            @RequestHeader(HEADER_USER_ID) Integer ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(required = false, defaultValue = FROM) Integer from,
            @RequestParam(required = false, defaultValue = SIZE) Integer size) {
        log.debug("[i] GET_ALL_BOOKINGS_FOR_OWNER\n OWNER_ID:{}, STATE:{}",
                ownerId, state);

        return service.getAllByOwner(
                ownerId,
                state,
                LocalDateTime.now(),
                checkPageable(from, size));
    }

    public static Pageable checkPageable(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Pageable incorrect");
        }
        return PageRequest.of(from / size, size);
    }
}
