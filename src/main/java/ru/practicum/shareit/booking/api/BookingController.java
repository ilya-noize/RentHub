package ru.practicum.shareit.booking.api;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.entity.BookingFilterByTemplate;

import java.util.List;

import static ru.practicum.shareit.ShareItApp.HEADER_USER_ID;

@RestController
public class BookingController {
    private BookingService service;

    public static final String CREATE_BOOKING_REQUEST = "/bookings";
    public static final String APPROVE_OR_REJECT_BOOKING = "/bookings/{id}";
    public static final String GET_BOOKING = "/bookings/{id}";
    public static final String ALL_BOOKING_FOR_USER = "/bookings?state={state}";
    public static final String ALL_BOOKING_FOR_OWNER = "/bookings/owner?state={state}";

    /**
     * Запрос может быть создан любым пользователем,<br/>
     * а затем подтверждён владельцем вещи.
     */
    @PostMapping(CREATE_BOOKING_REQUEST)
    public BookingDto create(
            @RequestBody BookingDto dto,
            @RequestHeader(HEADER_USER_ID) Integer userId) {
        dto.setBookerId(userId);

        return service.create(dto); // todo: add userId and itemId in signature
    }

    /**
     * Подтверждение или отклонение запроса на бронирование.<br/>
     * Может быть выполнено только владельцем вещи.<br/>
     * Затем статус бронирования становится либо APPROVED, либо REJECTED.
     *
     * @param id booking ID
     * @param userId user ID - Owner
     * @param approved Booking status (true = APPROVED / false = REJECTED)
     */
    @PatchMapping(APPROVE_OR_REJECT_BOOKING)
    public BookingDto update(
            @RequestParam Integer id,
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Boolean approved) {

        return service.update(id, userId, approved);
    }

    /**
     * Получение данных о конкретном бронировании (включая его статус).<br/>
     * Может быть выполнено либо автором бронирования,<br/>
     * либо владельцем вещи, к которой относится бронирование.
     */
    @GetMapping(GET_BOOKING)
    public BookingDto get(
            @RequestParam Integer id,
            @RequestHeader(HEADER_USER_ID) Integer userId) {

        return service.get(id, userId);
    }

    /**
     * Получение списка всех бронирований текущего пользователя.<br/>
     * Список предметов взятых в аренду когда-либо.
     */
    @GetMapping(ALL_BOOKING_FOR_USER)
    public List<BookingDto> getAllByUser(
            @PathVariable(required = false, value = "ALL") String state,
            @RequestHeader(HEADER_USER_ID) Integer userId) {

        return service.getAllByUser(BookingFilterByTemplate.valueOf(state.toUpperCase()), userId);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя.<br/>
     * Список предметов доступных для аренды от пользователя.
     */
    @GetMapping(ALL_BOOKING_FOR_OWNER)
    public List<BookingDto> getAllByOwner(
            @PathVariable(required = false, value = "ALL") String state,
            @RequestHeader(HEADER_USER_ID) Integer userId) {

        return service.getAllByOwner(BookingFilterByTemplate.valueOf(state), userId);
    }
}
