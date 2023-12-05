package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.api.BookingController;
import ru.practicum.shareit.item.api.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.api.UserController;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShareItTests {

    public static final String USER_WITH_ID_NOT_EXIST = "User with id:(%d) not exist";
    public static final String ITEM_WITH_ID_NOT_EXIST = "Item with id:(%d) not exist";

    @Autowired
    private UserController userController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private BookingController bookingController;
    @Autowired
    private ItemRequestController itemRequestController;

    @Test
    public void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(itemController).isNotNull();
        assertThat(bookingController).isNotNull();
        assertThat(itemRequestController).isNotNull();
    }

}
