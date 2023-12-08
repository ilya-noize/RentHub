package ru.practicum.shareit;

import org.jeasy.random.EasyRandom;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.BadRequestException;

/**
 * <h1>GLOBAL VARIABLES</h1>
 * <h2>Logs in tests</h2>
 * {@link ShareItApp#LOGGING_IN_TEST} Включает логирование в тестах (No args: false)<br/>
 * <h2>Headers</h2>
 * {@link ShareItApp#HEADER_USER_ID} Имя заголовка для userId <br/>
 * <h2>Errors</h2>
 * {@link ShareItApp#USER_WITH_ID_NOT_EXIST} Текст ошибки, если пользователь не существует <br/>
 * {@link ShareItApp#ITEM_WITH_ID_NOT_EXIST} Текст ошибки, если предмет не существует <br/>
 * {@link ShareItApp#BOOKING_WITH_ID_NOT_EXIST} Текст ошибки, если бронирование не существует <br/>
 * <h1>Настройки Spring Boot</h1>
 * {@link ShareItApp#switchOffBanner} Выключить Баннер Spring при старте
 */
@SpringBootApplication
public class ShareItApp {

    public static boolean LOGGING_IN_TEST;

    public static final String HEADER_USER_ID = "X-Sharer-User-Id";

    public static final String USER_WITH_ID_NOT_EXIST = "User with id:(%d) not exist";
    public static final String ITEM_WITH_ID_NOT_EXIST = "Item with id:(%d) not exist";
    public static final String REQUEST_WITH_ID_NOT_EXIST = "Request with id:(%d) not exist";
    public static final String BOOKING_WITH_ID_NOT_EXIST = "Booking with id:(%d) not exist";

    public static final String FROM = "0";
    public static final String SIZE = "10";
    public static final EasyRandom random = new EasyRandom();

    public static final String SEPARATOR_LINE = " -".repeat(40);

    private static final boolean switchOffBanner = true;

    public static void main(String[] args) {
        LOGGING_IN_TEST = args.length != 0;
        SpringApplication application = new SpringApplication(ShareItApp.class);
        if (switchOffBanner) {
            application.setBannerMode(Banner.Mode.OFF);
        }
        application.run(args);
    }

    public static Pageable checkPageable(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Pageable incorrect");
        }
        return PageRequest.of(from / size, size);
    }

}
