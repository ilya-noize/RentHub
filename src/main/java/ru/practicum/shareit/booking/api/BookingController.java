package ru.practicum.shareit.booking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingDtoRecord;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.HEADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController {
    public static final String CREATE_BOOKING_REQUEST = "/bookings";
    public static final String APPROVE_OR_REJECT_BOOKING = "/bookings/{id}";
    public static final String GET_BOOKING = "/bookings/{id}";
    public static final String ALL_BOOKING_FOR_USER = "/bookings";
    public static final String ALL_BOOKING_FOR_OWNER = "/bookings/owner";
    private final BookingService service;

    /**
     * Запрос может быть создан любым пользователем,<br/>
     * а затем подтверждён владельцем вещи.
     */
    @PostMapping(CREATE_BOOKING_REQUEST)
    public BookingDtoRecord create(
            @RequestHeader(HEADER_USER_ID)
            Integer userId,
            @RequestBody
            @Valid
            BookingDto dto) {
        log.info("Point: [{}]\nIncoming: Dto:{} UserId:{}", CREATE_BOOKING_REQUEST, dto, userId);

        BookingDtoRecord record = service.create(userId, dto);

        log.info("[i] CONTROLLER CREATE \n" +
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
    @PatchMapping(APPROVE_OR_REJECT_BOOKING)
    public BookingDtoRecord update(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Long id,
            @RequestParam Boolean approved) {

        log.info("[i] UPDATE\n USER_ID:{}, BOOKING_ID:{}, APPROVED:{}",
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
    public BookingDtoRecord get(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Long id) {

        return service.get(userId, id);
    }

    /**
     * Получение списка всех бронирований текущего пользователя.<br/>
     * Список предметов взятых в аренду когда-либо.
     *
     * @param bookerId User ID - Booker
     * @param state    Search filter
     */
    @GetMapping(ALL_BOOKING_FOR_USER)
    public List<BookingDtoRecord> getAllByUser(
            @RequestHeader(HEADER_USER_ID) Integer bookerId,
            @RequestParam(defaultValue = "ALL") String state) {

        return service.getAllByUser(
                bookerId,
                state.toUpperCase());
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя.<br/>
     * Список предметов доступных для аренды от пользователя.
     *
     * @param ownerId User ID - Owner
     * @param state   Search filter
     */
    @GetMapping(ALL_BOOKING_FOR_OWNER)
    public List<BookingDtoRecord> getAllByOwner(
            @RequestHeader(HEADER_USER_ID) Integer ownerId,
            @RequestParam(defaultValue = "ALL") String state) {

        return service.getAllByOwner(
                ownerId,
                state.toUpperCase());
    }
}
