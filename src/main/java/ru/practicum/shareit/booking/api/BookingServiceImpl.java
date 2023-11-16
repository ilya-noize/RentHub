package ru.practicum.shareit.booking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingDtoRecord;
import ru.practicum.shareit.booking.api.dto.BookingMapper;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.enums.BookingFilterByTemplate;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.RentalPeriodException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;
    private final LocalDateTime now = LocalDateTime.now();

    @Override
    public BookingDtoRecord create(Integer bookerId, BookingDto dto) {
        Integer itemId = dto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("Item with id:%s not found.",
                                        itemId)));

        if (!item.isAvailable()) {
            throw new AccessException("It is impossible to rent "
                    + "an item to which access is closed.");
        }

        User booker = userRepository.findById(bookerId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("User with id:%s not found.",
                                        bookerId)));

        boolean bookerIsOwnerTheItem = bookerId
                .equals(
                        item
                                .getOwner()
                                .getId());

        if (bookerIsOwnerTheItem) {
            throw new AccessException("Access denied."
                    + " You are owner this item");
        }

        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();


        if (start.equals(end)) {
            String error = "The effective date of the lease agreement"
                    + " coincides with its termination";
            throw new RentalPeriodException(error);
        }

        if (end.isBefore(start)) {
            String error = "The effective date of the lease agreement"
                    + " after its termination";
            throw new RentalPeriodException(error);
        }

        Booking booking = mapper.toEntity(dto, bookerId);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);

        booking = bookingRepository.save(booking);

        return getBookingDtoRecord(bookerId, booking);
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
    public BookingDtoRecord update(Integer ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("Booking with ID: %d not found",
                                        bookingId)));

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(
                    format("User with ID: %d not found",
                            ownerId));
        }

        Integer bookerId = booking.getBooker().getId();
        boolean ownerIsBooker = ownerId.equals(bookerId);

        if (ownerIsBooker) {
            throw new AccessException("Access denied.\n"
                    + "You cannot be the owner and the booker of this item at the same time");
        }

        booking.setStatus(approved ? APPROVED : REJECTED);
        bookingRepository
                .updateStatusById(
                        booking.getStatus(),
                        bookingId);

        return getBookingDtoRecord(bookerId, booking);
    }

    /**
     * @param userId    User ID
     * @param bookingId Booking ID
     * @return Booking
     */
    @Override
    public BookingDtoRecord get(Integer userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("Booking not found. Id:%s",
                                        bookingId)));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(
                    format("User not found. Id:%s",
                            userId));
        }

        Integer bookerId = booking
                .getBooker()
                .getId();
        Integer ownerId = booking.getItem()
                .getOwner()
                .getId();

        boolean isBooker = userId.equals(bookerId);
        boolean isOwner = userId.equals(ownerId);

        if (!isBooker && !isOwner) {
            throw new AccessException("Access denied.\n"
                    + "You a not the booker/owner of the item");
        }

        return getBookingDtoRecord(bookerId, booking);
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
     * @param stateIn  Фильтр поиска
     * @return Список бронирования
     */
    @Override
    public List<BookingDtoRecord> getAllByUser(Integer bookerId, String stateIn) {
        List<Booking> bookingList;
        BookingFilterByTemplate state = BookingFilterByTemplate
                .valueOf(stateIn);

        User user = userRepository
                .findById(bookerId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("User with id:%s not found.", bookerId)));

        switch (state) {
            case ALL:
                bookingList = bookingRepository
                        .findAllByBookerOrderByStartDesc(user);
                break;
            case PAST:
                bookingList = bookingRepository
                        .findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                                user,
                                now,
                                now);
                break;
            case FUTURE:
                bookingList = bookingRepository
                        .findAllByBookerAndStartAfterOrderByStartDesc(
                                user,
                                now);
                break;
            case CURRENT:
                bookingList = bookingRepository
                        .findAllByBookerAndEndBeforeOrderByStartDesc(
                                user,
                                now);
                break;
            case WAITING:
                bookingList = bookingRepository
                        .findAllByBookerAndStatusOrderByStartDesc(
                                user,
                                APPROVED);
                break;
            case REJECTED:
                bookingList = bookingRepository
                        .findAllByBookerAndStatusOrderByStartDesc(
                                user,
                                REJECTED);
                break;
            default:
                throw new StateException("Unknown state: " + stateIn);
        }
        return getCollect(bookingList);
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
     * @param ownerId user ID
     * @param stateIn Фильтр поиска
     * @return Список бронирования
     */
    @Override
    public List<BookingDtoRecord> getAllByOwner(Integer ownerId, String stateIn) {
        List<Booking> bookingList;
        BookingFilterByTemplate state = BookingFilterByTemplate.valueOf(stateIn);

        log.info("[i] GET_ALL_OWNER");
        User owner = userRepository.findById(ownerId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("User with id:%s not found.",
                                        ownerId)));

        log.info("[i] {}", state);
        switch (state) {
            case ALL:
                bookingList = bookingRepository
                        .findAllByItem_OwnerOrderByStartDesc(owner);
                break;
            case PAST:
                bookingList = bookingRepository
                        .findAllByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                                owner,
                                now,
                                now);
                break;
            case FUTURE:
                bookingList = bookingRepository
                        .findAllByItem_OwnerAndStartAfterOrderByStartDesc(
                                owner,
                                now);
                break;
            case CURRENT:
                bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
                                owner,
                                now);
                break;
            case WAITING:
                bookingList = bookingRepository
                        .findAllByItem_OwnerAndStatusOrderByStartDesc(
                                owner,
                                WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository
                        .findAllByItem_OwnerAndStatusOrderByStartDesc(
                                owner,
                                REJECTED);
                break;
            default:
                throw new StateException("Unknown state: " + stateIn);
        }
        log.info("[i] bookingList:{}", bookingList);
        return getCollect(bookingList);
    }

    private List<BookingDtoRecord> getCollect(List<Booking> bookingList) {

        return bookingList.stream()
                .map(
                        entity -> {
                            Integer bookerId = entity
                                    .getBooker()
                                    .getId();

                            return getBookingDtoRecord(bookerId, entity);
                        }
                )
                .collect(toList());
    }

    private BookingDtoRecord getBookingDtoRecord(Integer bookerId, Booking booking) {
        ItemDto itemDto = ItemDto.builder()
                .id(
                        booking
                                .getItem()
                                .getId())
                .name(
                        booking
                                .getItem()
                                .getName())
                .build();

        return mapper.toDtoRecord(booking, bookerId, itemDto);
    }
}
