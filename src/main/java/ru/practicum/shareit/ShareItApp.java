package ru.practicum.shareit;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * {@link ShareItApp#LOGGING_IS_NEEDED_IN_TEST} Включает логирование в тестах <br/>
 * {@link ShareItApp#HEADER_USER_ID} Имя заголовка для userId <br/>
 * {@link ShareItApp#USER_WITH_ID_NOT_EXIST} Текст ошибки, если пользователь не существует <br/>
 * {@link ShareItApp#ITEM_WITH_ID_NOT_EXIST} Текст ошибки, если предмет не существует <br/>
 * {@link ShareItApp#BOOKING_WITH_ID_NOT_EXIST} Текст ошибки, если бронирование не существует <br/>
 */
@SpringBootApplication
public class ShareItApp {

    public static final boolean LOGGING_IS_NEEDED_IN_TEST = true;

    public static final String HEADER_USER_ID = "X-Sharer-User-Id";
    public static final String USER_WITH_ID_NOT_EXIST = "User with id:(%d) not exist";
    public static final String ITEM_WITH_ID_NOT_EXIST = "Item with id:(%d) not exist";
    public static final String BOOKING_WITH_ID_NOT_EXIST = "Booking with id:(%d) not exist";

    public static void main(String[] args) {
//		SpringApplication application = new SpringApplication(ShareItApp.class);
//		application.setBannerMode(Banner.Mode.OFF);
//		application.run(args);
    }

}
