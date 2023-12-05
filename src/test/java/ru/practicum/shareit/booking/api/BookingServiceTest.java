package ru.practicum.shareit.booking.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingDtoRecord;
import ru.practicum.shareit.booking.api.dto.BookingMapper;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.enums.BookingFilterByTemplate;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.ShareItApp.*;
import static ru.practicum.shareit.booking.entity.enums.BookingFilterByTemplate.*;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.WAITING;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest extends InjectResources {

    private final LocalDateTime now = LocalDateTime
            // created at 2000 Jan, 1, PM12:00:00.0000
            .of(2000, 1, 1, 12, 0, 0, 0);
    private final int days = 7;
    private final int rentTime = 3;
    private final LocalDateTime startNext = now.plusDays(days);
    private final LocalDateTime endNext = startNext.plusDays(rentTime);

    Map<Integer, Item> itemStorage;
    Map<Integer, User> userStorage;

    @InjectMocks
    BookingServiceImpl bookingService;
    private Booking bookingEntity;
    private BookingDto nextBookingRequest;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    /**
     * User1 -> Items(1, 4, 7);
     * User2 -> Items(2, 5, 8);
     * User3 -> Items(3, 6, 9);
     * User4 -> Items(null);
     */
    @BeforeEach
    void createsEnvironmentObjects() {

        if (users.isEmpty()) {
            throw new RuntimeException("No Users Set.");
        }
        if (items.isEmpty()) {
            throw new RuntimeException("No Items Set.");
        }
        if (LOGGING_IS_NEEDED_IN_TEST) {
            System.out.println("- ".repeat(40));
            for (User u : users) {
                System.out.printf("User:[%d]\t%s%n", u.getId(), u);
            }
            System.out.println("- ".repeat(40));
            for (Item i : items) {
                System.out.printf("Item:[%d]\t%s%n", i.getId(), i);
            }
        }

        items.forEach(item -> {
            int itemId = item.getId();
            if (itemId % 3 == 1) {
                item.setOwner(users.get(0));
            } else if (itemId % 3 == 2) {
                item.setOwner(users.get(1));
            } else {
                item.setOwner(users.get(2));
            }
        });

        Map<Integer, List<Item>> ownerStorage = items.stream()
                .collect(Collectors.groupingBy(item -> item.getOwner().getId()));

        itemStorage = items.stream()
                .collect(toMap(Item::getId,
                        Function.identity(),
                        (first, second) -> first));

        userStorage = users.stream()
                .collect(toMap(User::getId,
                        Function.identity(),
                        (first, second) -> first));

        if (LOGGING_IS_NEEDED_IN_TEST) {
            System.out.println("- ".repeat(40));
            for (Integer userId : ownerStorage.keySet()) {
                System.out.printf("OwnerStorage by UserId:[%d]%n", userId);
                for (Item item : ownerStorage.get(userId)) {
                    System.out.printf("\tItemId:[%d]\t%s%n", item.getId(), item);
                }
            }
        }

        bookingEntity = Booking.builder()
                .start(startNext)
                .end(endNext)
                .item(items.get(0))
                .booker(users.get(1))
                .status(WAITING).build();

        nextBookingRequest = BookingDto.builder()
                .id(1L)
                .itemId(1)
                .start(startNext)
                .end(endNext).build();
    }

    @Test
    void create_whenValidBooking_thenReturnDtoRecord() {
        //given
        Item item = itemStorage.get(1);
        int itemId = item.getId();
        User booker = userStorage.get(2);
        int bookerId = booker.getId();

        BookingDto bookingRequest = nextBookingRequest;
        BookingDtoRecord bookingResponse;
        BookingDtoRecord bookingExpected = BookingDtoRecord.builder()
                .id(1L)
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .status(WAITING)
                .booker(new BookingDtoRecord.BookerDto(bookerId))
                .item(new BookingDtoRecord.ItemDto(itemId, item.getName())).build();

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        // validation: booking and start-end dates
        when(bookingMapper.toEntity(bookingRequest, 2))
                .thenReturn(bookingEntity);
        when(bookingRepository.save(bookingEntity))
                .thenReturn(bookingEntity);
        when(bookingMapper.toDtoRecord(bookingEntity))
                .thenReturn(bookingExpected);

        //when
        bookingResponse = bookingService.create(bookerId, bookingRequest);
        //then
        assertNotNull(bookingResponse.getId());
        assertEquals(bookingResponse.getStatus(), WAITING);
        assertEquals(bookingResponse.getItem().getId(), itemId);
        assertEquals(bookingResponse.getBooker().getId(), bookerId);
    }

    @Test
    void create_whenInvalidItem_thenReturnThrow() {
        //given
        int itemId = itemStorage.get(1).getId();
        int bookerId = userStorage.get(2).getId();

        BookingDto bookingRequest = nextBookingRequest;

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookerId, bookingRequest));
        //then
        assertEquals(e.getMessage(), format(ITEM_WITH_ID_NOT_EXIST, itemId));
    }

    @Test
    void create_whenInvalidUser_thenReturnThrow() {
        //given
        Item item = itemStorage.get(1);
        int itemId = item.getId();
        User booker = userStorage.get(2);
        int bookerId = booker.getId();

        BookingDto bookingRequest = nextBookingRequest;

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.empty());

        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookerId, bookingRequest));
        //then
        assertEquals(e.getMessage(), format(USER_WITH_ID_NOT_EXIST, bookerId));
    }

    @Test
    void update_REJECTED() {
        //given
        final BookingStatus status = BookingStatus.REJECTED;
        Item item = itemStorage.get(1);
        int itemId = item.getId();
        User owner = item.getOwner();
        int ownerId = owner.getId();
        User booker = userStorage.get(2);
        int bookerId = booker.getId();

        BookingDto bookingRequest = nextBookingRequest;
        BookingDtoRecord bookingResponse;
        BookingDtoRecord bookingExpected = BookingDtoRecord.builder()
                .id(1L)
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .status(status)
                .booker(new BookingDtoRecord.BookerDto(bookerId))
                .item(new BookingDtoRecord.ItemDto(itemId, item.getName())).build();
        long bookingId = bookingRequest.getId();
        Booking bookingUpdate = bookingEntity;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(ownerId))
                .thenReturn(true);
        doNothing().when(bookingRepository).updateStatusById(status, bookingId);
        when(bookingMapper.toDtoRecord(bookingUpdate))
                .thenReturn(bookingExpected);

        if (LOGGING_IS_NEEDED_IN_TEST) {
            System.out.printf("itemId: %d, ownerId: %d, bookerId: %d, bookingId: %d%n", itemId, ownerId, bookerId, bookingId);
            System.out.printf("bookingEntity:   %s%n", bookingEntity);
            System.out.printf("bookingRequest:  %s%n", bookingRequest);
            System.out.printf("bookingExpected: %s%n", bookingExpected);
        }
        // when
        bookingResponse = bookingService.update(ownerId, bookingId, false);
        // then
        assertEquals(status, bookingResponse.getStatus());
        System.out.printf("bookingResponse: %s%n", bookingResponse);

        verify(bookingRepository, times(1))
                .updateStatusById(status, bookingId);
    }

    @Test
    void update_whenInvalidBooking_thenReturnThrow() {
        //given
        int ownerId = itemStorage.get(1).getOwner().getId();

        BookingDto bookingRequest = nextBookingRequest;
        long bookingId = bookingRequest.getId();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());
        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.update(ownerId, bookingId, false));
        //then
        assertEquals(e.getMessage(), format(BOOKING_WITH_ID_NOT_EXIST, bookingId));

        verify(bookingRepository, never())
                .updateStatusById(BookingStatus.REJECTED, bookingId);
    }


    @Test
    void update_whenInvalidUser_thenReturnThrow() {
        //given
        int ownerId = itemStorage.get(1).getOwner().getId();

        BookingDto bookingRequest = nextBookingRequest;
        long bookingId = bookingRequest.getId();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(ownerId))
                .thenReturn(false);
        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.update(ownerId, bookingId, false));
        //then
        assertEquals(e.getMessage(), format(USER_WITH_ID_NOT_EXIST, ownerId));

        verify(bookingRepository, never())
                .updateStatusById(BookingStatus.REJECTED, bookingId);
    }

    @Test
    void update_APPROVED() {
        //given
        final BookingStatus status = APPROVED;
        Item item = itemStorage.get(1);
        int itemId = item.getId();
        User owner = item.getOwner();
        int ownerId = owner.getId();
        User booker = userStorage.get(2);
        int bookerId = booker.getId();

        BookingDto bookingRequest = nextBookingRequest;
        BookingDtoRecord bookingResponse;
        BookingDtoRecord bookingExpected = BookingDtoRecord.builder()
                .id(1L)
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .status(status)
                .booker(new BookingDtoRecord.BookerDto(bookerId))
                .item(new BookingDtoRecord.ItemDto(itemId, item.getName())).build();
        long bookingId = bookingRequest.getId();
        Booking bookingUpdate = bookingEntity;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(ownerId))
                .thenReturn(true);
        doNothing().when(bookingRepository).updateStatusById(status, bookingId);
        when(bookingMapper.toDtoRecord(bookingUpdate))
                .thenReturn(bookingExpected);
        if (LOGGING_IS_NEEDED_IN_TEST) {
            System.out.printf("itemId: %d, ownerId: %d, bookerId: %d, bookingId: %d%n", itemId, ownerId, bookerId, bookingId);
            System.out.printf("bookingEntity:   %s%n", bookingEntity);
            System.out.printf("bookingRequest:  %s%n", bookingRequest);
            System.out.printf("bookingExpected: %s%n", bookingExpected);
        }
        // when
        bookingResponse = bookingService.update(ownerId, bookingId, true);
        // then
        assertEquals(status, bookingResponse.getStatus());
        System.out.printf("bookingResponse: %s%n", bookingResponse);

        verify(bookingRepository, times(1))
                .updateStatusById(status, bookingId);
    }

    @Test
    void get_whenBooker_thenReturnDtoRecord() {
        Item item = itemStorage.get(1);
        int itemId = item.getId();
        User booker = userStorage.get(2);
        int bookerId = booker.getId();

        BookingDto bookingRequest = nextBookingRequest;
        BookingDtoRecord bookingResponse;
        BookingDtoRecord bookingExpected = BookingDtoRecord.builder()
                .id(1L)
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .status(WAITING)
                .booker(new BookingDtoRecord.BookerDto(bookerId))
                .item(new BookingDtoRecord.ItemDto(itemId, item.getName())).build();
        long bookingId = bookingRequest.getId();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(bookerId))
                .thenReturn(true);
        when(bookingMapper.toDtoRecord(bookingEntity))
                .thenReturn(bookingExpected);

        // when
        bookingResponse = bookingService.get(bookerId, bookingId);
        // then
        assertEquals(bookingResponse.getBooker().getId(), bookerId);
    }

    @Test
    void get_whenOwner_thenReturnDtoRecord() {
        Item item = itemStorage.get(1);
        int itemId = item.getId();
        User owner = item.getOwner();
        int ownerId = owner.getId();
        User booker = userStorage.get(2);
        int bookerId = booker.getId();

        BookingDto bookingRequest = nextBookingRequest;
        BookingDtoRecord bookingResponse;
        BookingDtoRecord bookingExpected = BookingDtoRecord.builder()
                .id(1L)
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .status(WAITING)
                .booker(new BookingDtoRecord.BookerDto(bookerId))
                .item(new BookingDtoRecord.ItemDto(itemId, item.getName())).build();
        long bookingId = bookingRequest.getId();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(ownerId))
                .thenReturn(true);
        when(bookingMapper.toDtoRecord(bookingEntity))
                .thenReturn(bookingExpected);

        // when
        bookingResponse = bookingService.get(ownerId, bookingId);
        // then
        int itemIdInBooking = bookingResponse.getItem().getId();
        int itemOwnerId = itemStorage.get(itemIdInBooking).getOwner().getId();
        assertEquals(itemOwnerId, ownerId);
    }

    @Test
    void getAllByUser_CURRENT() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // CURRENT
                .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, now, now))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByUser(bookerId, CURRENT.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByUser_PAST() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // PAST
                .findAllByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, now))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByUser(bookerId, PAST.toString(), now);
        // then
        assertEquals(1, response.size());
    }


    @Test
    void getAllByUser_ALL() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // ALL
                .findAllByBooker_IdOrderByStartDesc(
                        bookerId)).thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByUser(bookerId, ALL.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByUser_FUTURE() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // FUTURE
                .findAllByBooker_IdAndStartAfterOrderByStartDesc(bookerId, now))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByUser(bookerId, FUTURE.toString(), now);
        // then
        assertEquals(1, response.size());
    }


    @Test
    void getAllByUser_WAITING() {
        final BookingFilterByTemplate filterByTemplate = BookingFilterByTemplate.WAITING;
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // WAITING
                .findAllByBooker_IdAndStatusOrderByStartDesc(
                        bookerId, BookingStatus.WAITING))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByUser(bookerId, filterByTemplate.toString(), now);
        // then
        assertEquals(1, response.size());
    }


    @Test
    void getAllByUser_REJECTED() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // REJECTED
                .findAllByBooker_IdAndStatusOrderByStartDesc(
                        bookerId, BookingStatus.REJECTED))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByUser(bookerId, BookingFilterByTemplate.REJECTED.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByUser_whenInvalidUser_thenReturnThrow() {
        int bookerId = userStorage.get(2).getId();

        when(userRepository.existsById(bookerId)).thenReturn(false);
        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService
                        .getAllByUser(bookerId, BookingFilterByTemplate.REJECTED.toString(), now));
        //then
        assertEquals(e.getMessage(), format(USER_WITH_ID_NOT_EXIST, bookerId));
    }

    @Test
    void getAllByOwner_CURRENT() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository // CURRENT
                .findAllByItem_Owner_IdAndStartBeforeAndEndAfter(
                        ownerId, now, now, Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByOwner(ownerId, CURRENT.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_PAST() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);

        when(bookingRepository // PAST
                .findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerId, now))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByOwner(ownerId, PAST.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_ALL() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository // ALL
                .findAllByItem_Owner_IdOrderByStartDesc(ownerId))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByOwner(ownerId, ALL.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_FUTURE() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository // FUTURE
                .findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, now))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByOwner(ownerId, FUTURE.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_WAITING() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);

        when(bookingRepository // WAITING
                .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, WAITING))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByOwner(ownerId, BookingFilterByTemplate.WAITING.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_REJECTED() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);

        when(bookingRepository // REJECTED
                .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, REJECTED))
                .thenReturn(bookingList);
        // when
        List<BookingDtoRecord> response = bookingService
                .getAllByOwner(ownerId, BookingFilterByTemplate.REJECTED.toString(), now);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_whenInvalidUser_thenReturnThrow() {
        int ownerId = itemStorage.get(1).getOwner().getId();

        when(userRepository.existsById(ownerId)).thenReturn(false);
        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService
                        .getAllByOwner(ownerId, BookingFilterByTemplate.REJECTED.toString(), now));
        //then
        assertEquals(e.getMessage(), format(USER_WITH_ID_NOT_EXIST, ownerId));
    }
}