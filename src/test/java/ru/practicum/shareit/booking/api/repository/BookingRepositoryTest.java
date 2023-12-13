package ru.practicum.shareit.booking.api.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.enums.BookingStatus;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.WAITING;
import static ru.practicum.shareit.constants.Constants.RANDOM;


@DataJpaTest
class BookingRepositoryTest {
    private final Pageable pageable = Pageable.ofSize(10);
    private final LocalDateTime now = LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0);
    private final Sort sortStartAsc =
            Sort.by(Sort.Direction.ASC, "start");
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User getNewUser() {
        User owner = RANDOM.nextObject(User.class);
        return userRepository.save(owner);
    }

    private Item getNewItem(User owner) {
        Item item = RANDOM.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        return itemRepository.save(item);
    }

    private void getNewBookingInPast(Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(now.minusDays(7));
        booking.setEnd(now.minusDays(4));
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);
    }

    private void getNewBookingNearPresent(Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(now.minusDays(1));
        booking.setEnd(now.plusDays(1));
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);
    }

    private Booking getNewBookingInFuture(Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(now.plusDays(4));
        booking.setEnd(now.plusDays(7));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    private void prepareBooking(Item item1, Item item2) {
        User booker = getNewUser();

        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, APPROVED);
    }

    private List<Integer> getItemIds() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        prepareBooking(item1, item2);

        return List.of(item1.getId(), item2.getId());
    }

    @Test
    @DisplayName("GET ALL LAST")
    void findByItem_IdInAndStartAfterAndStatus() {
        List<Integer> ids = getItemIds();

        assertEquals(1,
                bookingRepository
                        .findByItem_IdInAndStartAfterAndStatus(
                                ids, now, APPROVED, sortStartAsc)
                        .size());
    }

    @Test
    @DisplayName("GET ALL NEXT")
    void findByItem_IdInAndStartLessThanEqualAndStatus() {
        List<Integer> ids = getItemIds();

        assertEquals(2,
                bookingRepository
                        .findByItem_IdInAndStartLessThanEqualAndStatus(
                                ids, now, APPROVED, sortStartAsc)
                        .size());
    }

    @Test
    @DisplayName("GET LAST")
    void findFirstByItem_IdAndStartAfterAndStatus() {
        List<Integer> ids = getItemIds();

        assertFalse(bookingRepository
                .findFirstByItem_IdAndStartAfterAndStatus(
                        ids.get(0), now, APPROVED, sortStartAsc)
                .isPresent());
    }

    @Test
    @DisplayName("GET NEXT")
    void findFirstByItem_IdAndStartLessThanEqualAndStatus() {
        List<Integer> ids = getItemIds();

        assertFalse(bookingRepository
                .findFirstByItem_IdAndStartAfterAndStatus(
                        ids.get(0), now, APPROVED, sortStartAsc)
                .isPresent());
    }

    @Test
    @DisplayName("ALL OWNER")
    void findAllByItem_Owner_IdOrderByStartDesc() {
    }

    @Test
    @DisplayName("ALL BOOKER")
    void findAllByBooker_IdOrderByStartDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        prepareBooking(item1, item2);

        assertEquals(3, bookingRepository
                .findAllByItem_Owner_IdOrderByStartDesc(owner.getId(), pageable)
                .size());
    }

    @Test
    @DisplayName("<APPROVED>, WAITING OWNER")
    void findAllByItem_Owner_IdAndStatusOrderByStartDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, APPROVED);

        assertEquals(3, bookingRepository
                .findAllByItem_Owner_IdAndStatusOrderByStartDesc(
                        owner.getId(), APPROVED, pageable)
                .size());
    }

    @Test
    @DisplayName("APPROVED, <WAITING> OWNER")
    void findAllByItem_Owner_IdAndStatusOrderByStartDesc_2() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, WAITING);

        assertEquals(1, bookingRepository
                .findAllByItem_Owner_IdAndStatusOrderByStartDesc(owner.getId(), WAITING, pageable)
                .size());
    }

    @Test
    @DisplayName("<APPROVED>, WAITING BOOKER")
    void findAllByBooker_IdAndStatusOrderByStartDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, APPROVED);

        assertEquals(3, bookingRepository
                .findAllByBooker_IdAndStatusOrderByStartDesc(booker.getId(), APPROVED, pageable)
                .size());
    }

    @Test
    @DisplayName("APPROVED, <WAITING> BOOKER")
    void findAllByBooker_IdAndStatusOrderByStartDesc_2() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, WAITING);

        assertEquals(1, bookingRepository
                .findAllByBooker_IdAndStatusOrderByStartDesc(booker.getId(), WAITING, pageable)
                .size());
    }

    @Test
    @DisplayName("PAST OWNER")
    void findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, WAITING);

        assertEquals(1, bookingRepository
                .findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(owner.getId(), now, pageable)
                .size());
    }

    @Test
    @DisplayName("PAST BOOKER")
    void findAllByBooker_IdAndEndBeforeOrderByStartDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, WAITING);

        assertEquals(1, bookingRepository
                .findAllByBooker_IdAndEndBeforeOrderByStartDesc(booker.getId(), now, pageable)
                .size());
    }

    @Test
    @DisplayName("FUTURE BOOKER")
    void findAllByBooker_IdAndStartAfterOrderByStartDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, WAITING);

        assertEquals(1, bookingRepository
                .findAllByBooker_IdAndStartAfterOrderByStartDesc(booker.getId(), now, pageable)
                .size());
    }

    @Test
    @DisplayName("FUTURE OWNER")
    void findAllByItem_Owner_IdAndStartAfterOrderByStartDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, WAITING);

        assertEquals(1, bookingRepository
                .findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(owner.getId(), now, pageable)
                .size());
    }

    @Test
    @DisplayName("CURRENT BOOKER")
    void findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, WAITING);

        assertEquals(1, bookingRepository
                .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(), now, now, pageable)
                .size());
    }

    @Test
    @DisplayName("CURRENT OWNER")
    void findAllByItem_Owner_IdAndStartBeforeAndEndAfter() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        getNewBookingInFuture(item2, booker, WAITING);

        assertEquals(1, bookingRepository
                .findAllByItem_Owner_IdAndStartBeforeAndEndAfterByStartDesc(
                        owner.getId(), now, now, pageable)
                .size());
    }

    @Test
    @DisplayName("UPDATE STATUS RIGHT NOW")
    void updateStatusById() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);
        Booking next = getNewBookingInFuture(item2, booker, WAITING);

        bookingRepository.updateStatusById(REJECTED, next.getId());

        Booking checking = bookingRepository.getReferenceById(next.getId());
        assertEquals(checking.getStatus(), REJECTED);
    }

    @Test
    @DisplayName("CHECK BOOKING FOR CREATE COMMENT")
    void existsCompletedBookingByTheUserOfTheItem() {
        User owner = getNewUser();
        Item item1 = getNewItem(owner);
        Item item2 = getNewItem(owner);

        User booker = getNewUser();
        getNewBookingInPast(item1, booker);
        getNewBookingNearPresent(item2, booker);

        getNewBookingInFuture(item2, booker, WAITING);

        assertTrue(bookingRepository.existsCompletedBookingByTheUserOfTheItem(
                item1.getId(), booker.getId(), APPROVED, now));
    }
}