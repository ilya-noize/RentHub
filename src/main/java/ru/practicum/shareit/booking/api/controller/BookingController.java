package ru.practicum.shareit.booking.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.api.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController {
    public static final String CREATE_BOOKING = "/bookings";
    public static final String UPDATE_STATUS_BOOKING = "/bookings/{id}";
    public static final String GET_BOOKING = "/bookings/{id}";
    public static final String GET_ALL_BOOKINGS_FOR_USER = "/bookings";
    public static final String GET_ALL_BOOKINGS_FOR_OWNER = "/bookings/owner";
    private final BookingService service;

    /**
     * Запрос может быть создан любым пользователем,<br/>
     * а затем подтверждён владельцем вещи.
     */
    @PostMapping(CREATE_BOOKING)
    public BookingDto create(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @RequestBody
            @Valid BookingSimpleDto dto) {
        log.info("Point: [{}]\nIncoming: Dto:{} UserId:{}", CREATE_BOOKING, dto, userId);

        BookingDto record = service.create(userId, dto);

        log.info("[i] CREATE_BOOKING \n" +
                        "ID:{}, START:{}, " +
                        "END:{}, STATUS:{}, BOOKER_ID:{}, " +
                        "ITEM_DTO:{}",
                record.getId(), record.getStart(),
                record.getEnd(), record.getStatus(), record.getBooker(), record.getItem());

        return record;
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
        log.info("[i] UPDATE_STATUS_BOOKING\n USER_ID:{}, BOOKING_ID:{}, APPROVED:{}",
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
        log.info("[i] GET_BOOKING\n USER_ID:{}, BOOKING_ID:{}",
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
    @Valid
    public List<BookingDto> getAllByUser(
            @RequestHeader(HEADER_USER_ID) Integer bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {
        log.info("[i] GET_ALL_BOOKINGS_FOR_USER\n BOOKER_ID:{}, STATE:{}",
                bookerId, state);

        return service.getAllByUser(
                bookerId,
                state.toUpperCase(),
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
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = FROM)
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SIZE)
            @Positive Integer size) {
        log.info("[i] GET_ALL_BOOKINGS_FOR_OWNER\n OWNER_ID:{}, STATE:{}",
                ownerId, state);

        return service.getAllByOwner(
                ownerId,
                state.toUpperCase(),
                LocalDateTime.now(),
                checkPageable(from, size));
    }
}
