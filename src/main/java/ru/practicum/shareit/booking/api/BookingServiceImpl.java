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
import ru.practicum.shareit.exception.*;
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
    public static final LocalDateTime NOW = LocalDateTime.now();
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    public BookingDtoRecord create(Integer bookerId, BookingDto dto) {
        Integer itemId = dto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("Item with id:%s not found.",
                                        itemId)));

        if (!item.isAvailable()) {
            throw new BadRequestException("It is impossible to rent "
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
            throw new BookingException("Access denied."
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

        log.info("[i] CREATE BOOKING (ID:{}) SUCCESSFUL (BOOKER_ID:{}, ITEM_ID:{})", booking.getId(), bookerId, dto.getItemId());

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
        Booking booking;
        boolean isNotWaitingStatus;
        boolean isNotExistUser;
        Integer bookerId;
        boolean ownerIsBooker;

        booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("Booking with ID: %d not found",
                                        bookingId)));

        isNotWaitingStatus = !WAITING.equals(booking.getStatus());
        if (isNotWaitingStatus) {
            throw new BadRequestException("The booking status has already been set.");
        }

        isNotExistUser = !userRepository.existsById(ownerId);
        if (isNotExistUser) {
            throw new NotFoundException(
                    format("User with ID: %d not found",
                            ownerId));
        }

        bookerId = booking.getBooker().getId();
        ownerIsBooker = ownerId.equals(bookerId);
        if (ownerIsBooker) {
            throw new BookingException("Access denied.\n"
                    + "You cannot be the owner and the booker of this item at the same time.");
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
        Booking booking;
        boolean isNotExistUser;
        Integer bookerId;
        Integer ownerId;
        boolean isBooker;
        boolean isOwner;

        booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new NotFoundException(
                                format("Booking with ID: %d not found.",
                                        bookingId)));

        isNotExistUser = !userRepository.existsById(userId);
        if (isNotExistUser) {
            throw new NotFoundException(
                    format("User with ID: %d not found",
                            userId));
        }

        bookerId = booking
                .getBooker()
                .getId();
        ownerId = booking.getItem()
                .getOwner()
                .getId();

        isBooker = userId.equals(bookerId);
        isOwner = userId.equals(ownerId);

        if (!isBooker && !isOwner) {
            throw new BookingException("Access denied.\n"
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

        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new NotFoundException(
                    format("User with id:%s not found.", bookerId)));

        BookingFilterByTemplate state =
                checkingInputParametersAndReturnEnumBookingFilterByTemplate(bookerId, stateIn);

        switch (state) {
            case ALL:
                bookingList = bookingRepository
                        .getAllByBooker_IdOrderByStartDesc(bookerId);
                break;
            case PAST: // wrong
                bookingList = bookingRepository
//                        .findByBookerAndEndBefore(
//                        )
                        .getAllByBookerAndEndBeforeOrderByStartDesc(
                        booker, NOW);

                log.info("[!!!!!] GET ALL BY BOOKER:{} STATE {} COUNT:{}", bookerId, state, bookingList.size());
                break;
            case FUTURE:
                bookingList = bookingRepository
                        .getAllByBooker_IdAndStartAfterOrderByStartDesc(
                                bookerId, NOW);
                break;
            case CURRENT: // wrong
                bookingList = bookingRepository
                        .getAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                                bookerId, NOW, NOW);

                log.info("[i] GET ALL BY BOOKER:{} STATE {} COUNT:{}", bookerId, state, bookingList.size());
                break;
            case WAITING:
                bookingList = bookingRepository
                        .getAllByBooker_IdAndStatusOrderByStartDesc(
                                bookerId, WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository
                        .getAllByBooker_IdAndStatusOrderByStartDesc(
                                bookerId, REJECTED);
                break;
            default:
                bookingList = List.of();
        }

        return getCollect(bookingList);
    }

    private BookingFilterByTemplate checkingInputParametersAndReturnEnumBookingFilterByTemplate(
            Integer userId, String stateIn) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format("User with id:%s not found.", userId));
        }
        try {
            return Enum.valueOf(BookingFilterByTemplate.class, stateIn);
        } catch (Exception e) {
            throw new StateException(stateIn);
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
     * @param ownerId user ID
     * @param stateIn Фильтр поиска
     * @return Список бронирования
     */
    @Override
    public List<BookingDtoRecord> getAllByOwner(Integer ownerId, String stateIn) {
        List<Booking> bookingList;

        BookingFilterByTemplate state =
                checkingInputParametersAndReturnEnumBookingFilterByTemplate(ownerId, stateIn);

        switch (state) {
            case ALL:
                bookingList = bookingRepository
                        .getAllByItem_Owner_IdOrderByStartDesc(ownerId);
                log.info("[i] GET ALL BY OWNER:{}  STATE {}  COUNT:{}", ownerId, state, bookingList.size());
                break;
            case PAST:// wrong
                bookingList = bookingRepository
                        .getAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
                                ownerId, NOW);
                log.info("[i] GET ALL BY OWNER:{}  STATE {}  COUNT:{}", ownerId, state, bookingList.size());
                break;
            case FUTURE:
                bookingList = bookingRepository
                        .getAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
                                ownerId, NOW);
                break;
            case CURRENT:// wrong
                bookingList = bookingRepository
                        .getAllByItem_Owner_IdAndStartBeforeOrderByStartDesc(
                                ownerId, NOW);//, NOW);
                log.info("[i] GET ALL BY OWNER:{}  STATE {}  COUNT:{}", ownerId, state, bookingList.size());
                break;
            case WAITING:
                bookingList = bookingRepository
                        .getAllByItem_Owner_IdAndStatusOrderByStartDesc(
                                ownerId, WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository
                        .getAllByItem_Owner_IdAndStatusOrderByStartDesc(
                                ownerId, REJECTED);
                break;
            default:
                bookingList = List.of();
        }

        return getCollect(bookingList);
    }

    private List<BookingDtoRecord> getCollect(List<Booking> bookingList) {

        return bookingList.stream()
                .map(
                        entity -> {
                            Integer bookerId = entity
                                    .getBooker()
                                    .getId();
                            log.info("[i] GET_COLLECT BOOKING_ID:{}, BOOKER_ID:{}, ITEM_ID:{}",
                                    entity.getId(),
                                    bookerId,
                                    entity.getItem().getId());

                            return getBookingDtoRecord(bookerId, entity);
                        }
                )
                .collect(toList());
    }

    private BookingDtoRecord getBookingDtoRecord(Integer bookerId, Booking entity) {
        log.info("[i] GET_BOOKING_DTO_RECORD BOOKING_ID:{}, BOOKER_ID:{}, ITEM_ID:{}",
                entity.getId(),
                bookerId,
                entity.getItem().getId());

        return mapper.toDtoRecord(entity);
    }
}
