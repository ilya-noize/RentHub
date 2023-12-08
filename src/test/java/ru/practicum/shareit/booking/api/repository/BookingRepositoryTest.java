package ru.practicum.shareit.booking.api.repository;

import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.api.repository.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.api.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;


@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private final EasyRandom random = new EasyRandom();


    private User createUser() {
        User owner = random.nextObject(User.class);
        return userRepository.save(owner);
    }

    private Booking createBooking(Item item, User booker) {
        Booking booking = random.nextObject(Booking.class);
        booking.setItem(item);
        booking.setBooker(booker);
        return bookingRepository.save(booking);
    }

    private Item createItem(User owner) {
        Item item = random.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(null);
        return itemRepository.save(item);
    }
}