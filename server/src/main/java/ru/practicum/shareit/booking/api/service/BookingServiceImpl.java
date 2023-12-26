package ru.practicum.shareit.booking.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingMapper;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.enums.BookingState;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.WAITING;
import static ru.practicum.shareit.constants.Constants.BOOKING_NOT_EXISTS;
import static ru.practicum.shareit.constants.Constants.ITEM_NOT_EXISTS;
import static ru.practicum.shareit.constants.Constants.USER_NOT_EXISTS;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long bookerId, BookingSimpleDto dto) {
        Long itemId = dto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        format(ITEM_NOT_EXISTS, itemId)));

        if (!item.isAvailable()) {
            throw new BadRequestException("It is impossible to rent "
                    + "an item to which access is closed.");
        }

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(
                        format(USER_NOT_EXISTS, bookerId)));

        boolean bookerIsOwnerTheItem = bookerId.equals(item.getOwner().getId());

        if (bookerIsOwnerTheItem) {
            throw new BookingException("Access denied."
                    + " You are owner this item");
        }

        Booking booking = BookingMapper.INSTANCE.toEntity(dto, bookerId);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);

        return BookingMapper.INSTANCE.toDto(
                bookingRepository.save(booking));
    }

    /**
     * Подтверждение или отклонение запроса на бронирование.<br/>
     * Может быть выполнено только владельцем вещи.<br/>
     * Затем статус бронирования становится либо APPROVED, либо REJECTED.
     *
     * @param ownerId   user ID - Owner
     * @param bookingId booking ID
     * @param approved  Booking status (true = APPROVED / false = REJECTED)
     * @return Бронирование с новым статусом
     */
    @Override
    public BookingDto update(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format(BOOKING_NOT_EXISTS, bookingId)));

        boolean isNotWaitingStatus = !WAITING.equals(booking.getStatus());
        if (isNotWaitingStatus) {
            throw new BadRequestException("The booking status has already been set.");
        }

        boolean isNotExistUser = !userRepository.existsById(ownerId);
        if (isNotExistUser) {
            throw new NotFoundException(
                    format(USER_NOT_EXISTS, ownerId));
        }

        Long bookerId = booking.getBooker().getId();
        boolean ownerIsBooker = ownerId.equals(bookerId);
        if (ownerIsBooker) {
            throw new BookingException("Access denied.\n"
                    + "You cannot be the owner and the booker of this item at the same time.");
        }

        booking.setStatus(approved ? APPROVED : REJECTED);
        bookingRepository
                .updateStatusById(booking.getStatus(), bookingId);

        return BookingMapper.INSTANCE.toDto(booking);
    }

    /**
     * @param userId    User ID
     * @param bookingId Booking ID
     * @return Booking
     */
    @Override
    public BookingDto get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        format(BOOKING_NOT_EXISTS, bookingId)));

        boolean isNotExistUser = !userRepository.existsById(userId);
        if (isNotExistUser) {
            throw new NotFoundException(
                    format(USER_NOT_EXISTS, userId));
        }

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        boolean isBooker = userId.equals(bookerId);
        boolean isOwner = userId.equals(ownerId);

        if (!isBooker && !isOwner) {
            throw new BookingException("Access denied.\n"
                    + "You a not the booker/owner of the item");
        }

        return BookingMapper.INSTANCE.toDto(booking);
    }

    /**
     * Список предметов, взятых пользователем.
     * <ul>
     *     Фильтр поиска:
     *     <li>ALL - все записи</li>
     *     <li>CURRENT - текущие предметы в аренде (start_date after NOW + end_date before NOW)</li>
     *     <li>PAST - прошлые бронирования (end_date before NOW)</li>
     *     <li>FUTURE - будущие бронирования (start_date after NOW)</li>
     *     <li>WAITING - бронирования в ожидании решения от владельца предмета (status ==)</li>
     *     <li>REJECTED - отказы в аренде от владельца предмета (status ==)</li>
     * </ul>
     *
     * @param bookerId user ID
     * @param state  Фильтр поиска
     * @param now      Точное время
     * @param pageable Постранично
     * @return Список бронирования
     */
    @Override
    public List<BookingDto> getAllByUser(Long bookerId, BookingState state, LocalDateTime now, Pageable pageable) {
        List<Booking> bookings = new ArrayList<>();

        checkingUserId(bookerId);

        switch (state) {
            case CURRENT:
                bookings = bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                                now, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                        bookerId, now, pageable);
                break;
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(
                        bookerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(
                        bookerId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(
                        bookerId, WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(
                        bookerId, REJECTED, pageable);
                break;
        }

        return getListBookingDtoRecord(bookings);
    }

    private void checkingUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format(USER_NOT_EXISTS, userId));
        }
    }

    /**
     * Список предметов, доступных в аренду пользователем.
     * <ul>
     *     Фильтр поиска:
     *     <li>ALL - все записи</li>
     *     <li>CURRENT - текущие предметы в аренде (start_date after NOW + end_date before NOW)</li>
     *     <li>PAST - прошлые бронирования (end_date before NOW)</li>
     *     <li>FUTURE - будущие бронирования (start_date after NOW)</li>
     *     <li>WAITING - бронирования в ожидании принятия решения (status ==)</li>
     *     <li>REJECTED - отказы в аренде от владельца предмета (status ==)</li>
     * </ul>
     *
     * @param ownerId  user ID
     * @param state  Фильтр поиска
     * @param now      Точное время
     * @param pageable Постранично
     * @return Список бронирования
     */
    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, BookingState state, LocalDateTime now, Pageable pageable) {
        List<Booking> bookings = new ArrayList<>();

        checkingUserId(ownerId);

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterByStartDesc(ownerId,
                        now, now, pageable);

                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
                        ownerId, now, pageable);
                break;
            case ALL:
                bookings = bookingRepository
                        .findAllByItem_Owner_IdOrderByStartDesc(
                                ownerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
                                ownerId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findAllByItem_Owner_IdAndStatusOrderByStartDesc(
                                ownerId, WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findAllByItem_Owner_IdAndStatusOrderByStartDesc(
                                ownerId, REJECTED, pageable);
                break;
        }

        return getListBookingDtoRecord(bookings);
    }

    private List<BookingDto> getListBookingDtoRecord(List<Booking> bookingList) {

        return bookingList.stream()
                .map(BookingMapper.INSTANCE::toDto)
                .collect(toList());
    }
}
