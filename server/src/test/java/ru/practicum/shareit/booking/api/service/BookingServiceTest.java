package ru.practicum.shareit.booking.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.api.dto.BookingDto;
import ru.practicum.shareit.booking.api.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.api.repository.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.enums.BookingState;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.utils.InjectResources;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.entity.enums.BookingState.ALL;
import static ru.practicum.shareit.booking.entity.enums.BookingState.CURRENT;
import static ru.practicum.shareit.booking.entity.enums.BookingState.FUTURE;
import static ru.practicum.shareit.booking.entity.enums.BookingState.PAST;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest extends InjectResources {

    private final Pageable pageable = Pageable.ofSize(10);
    private final int days = 7;
    private final int rentTime = 3;
    private final LocalDateTime startNext = now.plusDays(days);
    private final LocalDateTime endNext = startNext.plusDays(rentTime);


    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private Booking bookingEntity;
    private BookingSimpleDto nextBookingRequest;


    @BeforeEach
    void createsEnvironmentObjectS() {
        bookingEntity = Booking.builder()
                .start(startNext)
                .end(endNext)
                .item(items.get(1))
                .booker(users.get(2))
                .status(WAITING).build();

        nextBookingRequest = BookingSimpleDto.builder()
                .id(1L)
                .itemId(1)
                .start(startNext)
                .end(endNext).build();
    }

    @Test
    void create_whenInvalidItem_thenReturnThrow() {
        //given
        int itemId = itemStorage.get(1).getId();
        int bookerId = userStorage.get(2).getId();

        BookingSimpleDto bookingRequest = nextBookingRequest;

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookerId, bookingRequest));
        //then
        assertEquals(e.getMessage(), format(Constants.ITEM_NOT_EXISTS, itemId));
    }

    @Test
    void create_whenInvalidUser_thenReturnThrow() {
        //given
        Item item = itemStorage.get(1);
        int itemId = item.getId();
        User booker = userStorage.get(2);
        int bookerId = booker.getId();

        BookingSimpleDto bookingRequest = nextBookingRequest;

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.empty());

        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookerId, bookingRequest));
        //then
        assertEquals(e.getMessage(), format(Constants.USER_NOT_EXISTS, bookerId));
    }


    @Test
    void update_REJECTED() {
        //given
        final BookingStatus status = REJECTED;
        Item item = itemStorage.get(1);
        int itemId = item.getId();
        User owner = item.getOwner();
        int ownerId = owner.getId();
        User booker = userStorage.get(2);
        int bookerId = booker.getId();

        BookingSimpleDto bookingRequest = nextBookingRequest;
        BookingDto bookingResponse;
        BookingDto bookingExpected = BookingDto.builder()
                .id(1L)
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .status(status)
                .booker(new BookingDto.BookerDto(bookerId))
                .item(new BookingDto.ItemDto(itemId, item.getName())).build();
        long bookingId = bookingRequest.getId();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(ownerId))
                .thenReturn(true);
        doNothing().when(bookingRepository).updateStatusById(status, bookingId);

        if (Constants.LOGGING_IN_TEST) {
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

        BookingSimpleDto bookingRequest = nextBookingRequest;
        long bookingId = bookingRequest.getId();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());
        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.update(ownerId, bookingId, false));
        //then
        assertEquals(e.getMessage(), format(Constants.BOOKING_NOT_EXISTS, bookingId));

        verify(bookingRepository, never())
                .updateStatusById(REJECTED, bookingId);
    }


    @Test
    void update_whenInvalidUser_thenReturnThrow() {
        //given
        int ownerId = itemStorage.get(1).getOwner().getId();

        BookingSimpleDto bookingRequest = nextBookingRequest;
        long bookingId = bookingRequest.getId();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(ownerId))
                .thenReturn(false);
        //when
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.update(ownerId, bookingId, false));
        //then
        assertEquals(e.getMessage(), format(Constants.USER_NOT_EXISTS, ownerId));

        verify(bookingRepository, never())
                .updateStatusById(REJECTED, bookingId);
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

        BookingSimpleDto bookingRequest = nextBookingRequest;
        BookingDto bookingResponse;
        BookingDto bookingExpected = BookingDto.builder()
                .id(1L)
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .status(status)
                .booker(new BookingDto.BookerDto(bookerId))
                .item(new BookingDto.ItemDto(itemId, item.getName())).build();
        long bookingId = bookingRequest.getId();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(ownerId))
                .thenReturn(true);
        doNothing().when(bookingRepository).updateStatusById(status, bookingId);
        if (Constants.LOGGING_IN_TEST) {
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
    void get_whenOwner_thenReturnDtoRecord() {
        Item item = itemStorage.get(1);
        User owner = item.getOwner();
        int ownerId = owner.getId();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingEntity));
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        // when
        BookingException e = assertThrows(BookingException.class, () -> bookingService.get(ownerId, 1L));
        // then
        assertEquals(e.getMessage(), "Access denied.\n" +
                "You a not the booker/owner of the item");
    }

    @Test
    void getAllByUser_CURRENT() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // CURRENT
                .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, now, now, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByUser(bookerId, CURRENT, now, pageable);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByUser_PAST() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // PAST
                .findAllByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, now, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByUser(bookerId, PAST, now, pageable);
        // then
        assertEquals(1, response.size());
    }


    @Test
    void getAllByUser_ALL() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // ALL
                .findAllByBooker_IdOrderByStartDesc(bookerId, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByUser(bookerId, ALL, now, pageable);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByUser_FUTURE() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // FUTURE
                .findAllByBooker_IdAndStartAfterOrderByStartDesc(bookerId, now, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByUser(bookerId, FUTURE, now, pageable);
        // then
        assertEquals(1, response.size());
    }


    @Test
    void getAllByUser_WAITING() {
        int bookerId = userStorage.get(2).getId();

        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository // WAITING
                .findAllByBooker_IdAndStatusOrderByStartDesc(
                        bookerId, WAITING, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByUser(bookerId, BookingState.WAITING, now, pageable);
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
                        bookerId, REJECTED, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByUser(bookerId,
                        BookingState.REJECTED,
                        now,
                        pageable);
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
                        .getAllByUser(bookerId,
                                BookingState.REJECTED,
                                now,
                                pageable));
        //then
        assertEquals(e.getMessage(), format(Constants.USER_NOT_EXISTS, bookerId));
    }

    @Test
    void getAllByOwner_CURRENT() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository // CURRENT
                .findAllByItem_Owner_IdAndStartBeforeAndEndAfterByStartDesc(
                        ownerId, now, now, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByOwner(ownerId, CURRENT, now, pageable);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_PAST() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);

        when(bookingRepository // PAST
                .findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerId, now, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByOwner(ownerId, PAST, now, pageable);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_ALL() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository // ALL
                .findAllByItem_Owner_IdOrderByStartDesc(ownerId, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByOwner(ownerId, ALL, now, pageable);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_FUTURE() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository // FUTURE
                .findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, now, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByOwner(ownerId, FUTURE, now, pageable);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_WAITING() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);

        when(bookingRepository // WAITING
                .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, WAITING, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByOwner(ownerId, BookingState.WAITING, now, pageable);
        // then
        assertEquals(1, response.size());
    }

    @Test
    void getAllByOwner_REJECTED() {
        int ownerId = itemStorage.get(1).getOwner().getId();
        List<Booking> bookingList = List.of(bookingEntity);

        when(userRepository.existsById(ownerId)).thenReturn(true);

        when(bookingRepository // REJECTED
                .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, REJECTED, pageable))
                .thenReturn(bookingList);
        // when
        List<BookingDto> response = bookingService
                .getAllByOwner(ownerId, BookingState.REJECTED, now, pageable);
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
                        .getAllByOwner(ownerId,
                                BookingState.REJECTED,
                                now,
                                pageable));
        //then
        assertEquals(e.getMessage(), format(Constants.USER_NOT_EXISTS, ownerId));
    }

    @Test
    @Disabled
    void getAllByOwner_whenInvalidState_thenReturnThrow() {
        BookingState state = PAST;//"UNSUPPORTED";
        int ownerId = itemStorage.get(1).getOwner().getId();

        when(userRepository.existsById(ownerId)).thenReturn(true);
        //when
        StateException e = assertThrows(StateException.class,
                () -> bookingService
                        .getAllByOwner(ownerId, state, now, pageable));
        //then
        assertEquals(e.getMessage(), format("Unknown state: %s", state));
    }
}