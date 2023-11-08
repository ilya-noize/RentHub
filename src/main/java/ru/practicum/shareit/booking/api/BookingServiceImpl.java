package ru.practicum.shareit.booking.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingMapper;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingFilterByTemplate;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.RentalPeriodException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.booking.entity.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final JpaRepository<User, Integer> userRepository;
    private final JpaRepository<Item, Integer> itemRepository;
    private final BookingMapper mapper;
    private final LocalDateTime now = LocalDateTime.now();
    private final Sort sortByStartDesc = Sort.by(DESC, "start_timestamp");

    @Override
    public BookingDto create(BookingDto dto) { // todo: add userId and itemId in signature
        Integer itemId = dto.getItemId();
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(format("Item with id:%s not found.", itemId));
        }

        Integer userId = dto.getBookerId();
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format("User with id:%s not found.", userId));
        }

        Item item = itemRepository.getReferenceById(itemId);
        if (!item.isAvailable()) {
            throw new AccessException("Невозможно взять предмет в аренду к которому закрыт доступ.");
        }

        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        if (start.isAfter(end)) {
            String error = format("The effective date (%tc) of the lease agreement"
                    + " after its termination (%tc)", start, end);
            throw new RentalPeriodException(error);
        }
        if (start.equals(end)) {
            String error = format("The effective date (%tc) of the lease agreement"
                    + " coincides with its termination (%tc)", start, end);
            throw new RentalPeriodException(error);
        }

        return mapper.toDto(
                bookingRepository.save(
                        mapper.toEntity(dto)));// todo: add userId and itemId in signature
    }

    /**
     * Подтверждение или отклонение запроса на бронирование.<br/>
     * Может быть выполнено только владельцем вещи.<br/>
     * Затем статус бронирования становится либо APPROVED, либо REJECTED.
     *
     * @param id       booking ID
     * @param userId   user ID - Owner
     * @param approved Booking status (true = APPROVED / false = REJECTED)
     * @return Обновлённое бронирование
     */
    @Override
    public BookingDto update(Integer id, Integer userId, Boolean approved) { // todo: add userId and itemId in signature
        if (!bookingRepository.existsById(id)) {
            throw new NotFoundException(format("Booking with ID: %d not found", id));
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format("User with ID: %d not found", userId));
        }
        Booking booking = bookingRepository.getReferenceById(id);
        Integer itemId = booking.getItem().getId();
        Item item = itemRepository.getReferenceById(itemId);
        if (!item.getOwnerId().equals(userId)) {
            throw new AccessException("Access denied. You are not owner this item");
        }
        BookingStatus status = approved ? APPROVED : REJECTED;
        bookingRepository.updateStatusById(status, id);

        return mapper.toDto(bookingRepository.getReferenceById(id)); // todo: add userId and itemId in signature
    }

    @Override
    public BookingDto get(Integer id, Integer userId) {
        if (!bookingRepository.existsById(id)) {
            throw new NotFoundException(format("Booking not found. Id:%s", id));
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format("User not found. Id:%s", userId));
        }

        Booking booking = bookingRepository.getReferenceById(id);

        Item item = itemRepository.getReferenceById(booking.getItem().getId());

        // Если userId = владелец вещи, то список должен быть с тремя статусами
        if (item.getOwnerId().equals(userId)) {
            return BookingDto.builder() // todo: BookingDtoStates ? Должно возвращаться BookingDto !
                    .id(id)
                    .itemId(booking.getItem().getId())
                    .bookerId(userId)
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    //.states() // todo: Понять откуда это взять и где хранить
                    .build();
        } else {
            return BookingDto.builder()
                    .id(id)
                    .itemId(booking.getItem().getId())
                    .bookerId(userId)
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .status(booking.getStatus())
                    .build();
        }
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
     * @param state  Фильтр поиска
     * @param userId user ID
     * @return Список бронирования
     */
    @Override
    public List<BookingDto> getAllByUser(BookingFilterByTemplate state, Integer userId) {
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBooker_Id(
                        userId, sortByStartDesc);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBooker_IdAndStart_TimestampBeforeAndEnd_TimestampAfter(
                        userId, now, now, sortByStartDesc);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBooker_IdAndStart_TimestampAfter(
                        userId, now, sortByStartDesc);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBooker_IdAndEnd_TimestampBefore(
                        userId, now, sortByStartDesc);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBooker_IdAndStatus(
                        userId, APPROVED, sortByStartDesc);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBooker_IdAndStatus(
                        userId, REJECTED, sortByStartDesc);
                break;
            default:
                bookingList = List.of();
        }
        return bookingList.stream()
                .map(mapper::toDto) // todo: add userId and itemId in signature
                .collect(toList());
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
     * @param state  Фильтр поиска
     * @param userId user ID
     * @return Список бронирования
     */
    @Override
    public List<BookingDto> getAllByOwner(BookingFilterByTemplate state, Integer userId) {
        List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwner_Id(
                        userId, sortByStartDesc);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwner_IdAndStart_TimestampBeforeAndEnd_TimestampAfter(
                        userId, now, now, sortByStartDesc);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwner_IdAndStart_TimestampAfter(
                        userId, now, sortByStartDesc);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwner_IdAndEnd_TimestampBefore(
                        userId, now, sortByStartDesc);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwner_IdAndStatus(
                        userId, WAITING, sortByStartDesc);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwner_IdAndStatus(
                        userId, REJECTED, sortByStartDesc);
                break;
            default:
                result = List.of();
        }
        return result.stream()
                .map(mapper::toDto) // todo: add bookerId and itemId in signature
                .collect(toList());
    }
}
