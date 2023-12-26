package ru.practicum.shareit.constants;

import org.jeasy.random.EasyRandom;

/**
 * <h1>GLOBAL VARIABLES - SERVER</h1>
 * <h2>Logs in tests</h2>
 * {@link Constants#LOG_SEPARATOR} Разделитель логов <br/>
 * {@link Constants#LOGGING_IN_TEST} Включает логирование в тестах (No args: false)<br/>
 * {@link Constants#FROM} Константа pageable <br/>
 * {@link Constants#SIZE} Константа pageable <br/>
 * {@link Constants#RANDOM} Random для тестов <br/>
 * <h2>Headers</h2>
 * {@link Constants#HEADER_USER_ID} Имя заголовка для userId <br/>
 * <h2>Errors</h2>
 * {@link Constants#USER_NOT_EXISTS} Текст ошибки, если пользователь не существует <br/>
 * {@link Constants#ITEM_NOT_EXISTS} Текст ошибки, если предмет не существует <br/>
 * {@link Constants#REQUEST_NOT_EXISTS} Текст ошибки, если запрос не существует <br/>
 * {@link Constants#BOOKING_NOT_EXISTS} Текст ошибки, если бронирование не существует <br/>
 * <h2>The URLs for the endpoint in controller</h2>
 * <h3>Booking Controller</h3>
 * {@link Constants#CREATE_BOOKING} Создание бронирования <br/>
 * {@link Constants#UPDATE_STATUS_BOOKING} Изменить статус бронирования <br/>
 * {@link Constants#GET_BOOKING}    Посмотреть бронирование <br/>
 * {@link Constants#GET_ALL_BOOKINGS_FOR_USER}  Посмотреть бронирования от имени пользователя <br/>
 * {@link Constants#GET_ALL_BOOKINGS_FOR_OWNER} Посмотреть бронирования от имени владельца предмета <br/>
 * <h3>Item Controller</h3>
 * {@link Constants#CREATE_ITEM} Создать предмет <br/>
 * {@link Constants#UPDATE_ITEM} Изменить предмет <br/>
 * {@link Constants#GET_ITEM} Посмотреть предмет <br/>
 * {@link Constants#SEARCH_ITEM} Поиск предмета <br/>
 * {@link Constants#GET_ALL_ITEMS} Посмотреть все предметы <br/>
 * {@link Constants#CREATE_COMMENT} Оставить комментарий для предмета <br/>
 * <h3>ItemRequest Controller</h3>
 * {@link Constants#CREATE_REQUEST} Создать запрос на предмет <br/>
 * {@link Constants#GET_BY_REQUESTER} Посмотреть запрос на предмет от имени запрашиваемого <br/>
 * {@link Constants#GET_REQUEST} Посмотреть запрос пользователя <br/>
 * {@link Constants#GET_ALL_REQUESTS} Посмотреть все запросы <br/>
 * <h3>Items Controller</h3>
 * {@link Constants#CREATE_USER} Создать пользователя <br/>
 * {@link Constants#UPDATE_USER} Изменить пользователя <br/>
 * {@link Constants#GET_USER}   Посмотреть пользователя <br/>
 * {@link Constants#DELETE_USER} Удалить пользователя <br/>
 * {@link Constants#GET_ALL_USERS} Посмотреть всех пользователей <br/>
 */
public interface Constants {
    boolean LOGGING_IN_TEST = false;
    String LOG_SEPARATOR = " -".repeat(40);
    EasyRandom RANDOM = new EasyRandom();
    String FROM = "0";
    String SIZE = "10";
    String HEADER_USER_ID = "X-Sharer-User-Id";
    String USER_NOT_EXISTS = "User with id:(%d) not exist";
    String ITEM_NOT_EXISTS = "Item with id:(%d) not exist";
    String REQUEST_NOT_EXISTS = "Request with id:(%d) not exist";
    String BOOKING_NOT_EXISTS = "Booking with id:(%d) not exist";
    String CREATE_BOOKING = "/bookings";
    String UPDATE_STATUS_BOOKING = "/bookings/{id}";
    String GET_BOOKING = "/bookings/{id}";
    String GET_ALL_BOOKINGS_FOR_USER = "/bookings";
    String GET_ALL_BOOKINGS_FOR_OWNER = "/bookings/owner";
    String CREATE_ITEM = "/items";
    String UPDATE_ITEM = "/items/{id}";
    String GET_ITEM = "/items/{id}";
    String SEARCH_ITEM = "/items/search";
    String GET_ALL_ITEMS = "/items";
    String CREATE_COMMENT = "/items/{id}/comment";
    String CREATE_REQUEST = "/requests";
    String GET_BY_REQUESTER = "/requests";
    String GET_REQUEST = "/requests/{id}";
    String GET_ALL_REQUESTS = "/requests/all";
    String CREATE_USER = "/users";
    String UPDATE_USER = "/users/{id}";
    String GET_USER = "/users/{id}";
    String DELETE_USER = "/users/{id}";
    String GET_ALL_USERS = "/users";
}
