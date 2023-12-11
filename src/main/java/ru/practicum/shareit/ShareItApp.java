package ru.practicum.shareit;

import org.jeasy.random.EasyRandom;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.BadRequestException;

/**
 * <h1>GLOBAL VARIABLES</h1>
 * <h2>Logs in tests</h2>
 * {@link ShareItApp#SEPARATOR_LINE} Разделитель логов
 * {@link ShareItApp#LOGGING_IN_TEST} Включает логирование в тестах (No args: false)<br/>
 * <h2>Headers</h2>
 * {@link ShareItApp#HEADER_USER_ID} Имя заголовка для userId <br/>
 * <h2>Errors</h2>
 * {@link ShareItApp#USER_NOT_EXISTS} Текст ошибки, если пользователь не существует <br/>
 * {@link ShareItApp#ITEM_NOT_EXISTS} Текст ошибки, если предмет не существует <br/>
 * {@link ShareItApp#REQUEST_NOT_EXISTS} Текст ошибки, если запрос не существует <br/>
 * {@link ShareItApp#BOOKING_NOT_EXISTS} Текст ошибки, если бронирование не существует <br/>
 * {@link ShareItApp#FROM} Константа pageable <br/>
 * {@link ShareItApp#SIZE} Константа pageable <br/>
 * {@link ShareItApp#RANDOM} Random для тестов <br/>
 */
@SpringBootApplication
public class ShareItApp {
    public static boolean LOGGING_IN_TEST = false;
    public static final String SEPARATOR_LINE = " -".repeat(40);

    public static final String HEADER_USER_ID = "X-Sharer-User-Id";

    public static final String USER_NOT_EXISTS = "User with id:(%d) not exist";
    public static final String ITEM_NOT_EXISTS = "Item with id:(%d) not exist";
    public static final String REQUEST_NOT_EXISTS = "Request with id:(%d) not exist";
    public static final String BOOKING_NOT_EXISTS = "Booking with id:(%d) not exist";

    public static final String FROM = "0";
    public static final String SIZE = "10";

    public static final EasyRandom RANDOM = new EasyRandom();


    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

    public static Pageable checkPageable(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Pageable incorrect");
        }
        return PageRequest.of(from / size, size);
    }
}
