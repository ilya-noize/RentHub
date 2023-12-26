package ru.practicum.shareit.booking.api.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.practicum.shareit.booking.entity.enums.BookingStatus.WAITING;
import static ru.practicum.shareit.constants.Constants.RANDOM;

class BookingMapperTest {
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void toEntity() {
        BookingSimpleDto bookingSimpleDto = RANDOM.nextObject(BookingSimpleDto.class);
        User booker = RANDOM.nextObject(User.class);

        Booking expected = BookingMapper.INSTANCE.toEntity(bookingSimpleDto, booker.getId());

        assertNull(BookingMapper.INSTANCE.toEntity(null, null));

        Booking bookingNull = BookingMapper.INSTANCE.toEntity(null, 1L);
        Booking expectedNull = Booking.builder()
                .id(null)
                .start(null)
                .end(null)
                .status(null).build();

        assertNotEquals(expectedNull, bookingNull);
        assertNull(bookingNull.getId());
        assertNull(bookingNull.getStart());
        assertNull(bookingNull.getEnd());
        assertNull(bookingNull.getStatus());


        assertNotNull(expected);
        assertEquals(expected.getId(), bookingSimpleDto.getId());
        assertEquals(expected.getStart(), bookingSimpleDto.getStart());
        assertEquals(expected.getEnd(), bookingSimpleDto.getEnd());
        assertEquals(expected.getItem().getId(), bookingSimpleDto.getItemId());
    }

    @Test
    void toDto() {
        User booker = RANDOM.nextObject(User.class);
        booker.setId(1L);
        User owner = RANDOM.nextObject(User.class);
        owner.setId(2L);
        Item item = RANDOM.nextObject(Item.class);
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequest(null);
        Booking booking = Booking.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(3))
                .item(item)
                .status(WAITING).build();
        BookingDto expected = BookingMapper.INSTANCE.toDto(booking);

        assertNull(BookingMapper.INSTANCE.toDto(null));

        assertNotNull(expected);
        assertEquals(expected.getId(), booking.getId());
        assertEquals(expected.getStart(), booking.getStart());
        assertEquals(expected.getEnd(), booking.getEnd());
        assertEquals(expected.getItem().getId(), booking.getItem().getId());
        assertEquals(expected.getItem().getName(), booking.getItem().getName());
        assertEquals(expected.getStatus(), booking.getStatus());
    }

    @Test
    void toItemDto() {
        User booker = RANDOM.nextObject(User.class);
        booker.setId(1L);
        User owner = RANDOM.nextObject(User.class);
        owner.setId(2L);
        Item item = RANDOM.nextObject(Item.class);
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequest(null);
        Booking booking = Booking.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(3))
                .item(item)
                .status(WAITING).build();
        BookingItemDto expected = BookingMapper.INSTANCE.toItemDto(booking);

        assertNull(BookingMapper.INSTANCE.toItemDto(null));

        assertNotNull(expected);
        assertEquals(expected.getId(), booking.getId());
        assertEquals(expected.getStart(), booking.getStart());
        assertEquals(expected.getEnd(), booking.getEnd());
        assertEquals(expected.getItemId(), booking.getItem().getId());
    }
}
