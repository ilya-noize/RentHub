package ru.practicum.shareit.constants;

/**
 * <h1>GLOBAL VARIABLES - GATEWAY</h1>
 * <h2>Logs in tests</h2>
 * {@link #FROM} Константа pageable <br/>
 * {@link #SIZE} Константа pageable <br/>
 * <h2>Headers</h2>
 * {@link #HEADER_USER_ID} Имя заголовка для userId <br/>
 * <h2>The URLs for the endpoint in controller</h2>
 * <h3>Booking Controller</h3>
 * {@link #CREATE_BOOKING} Создание бронирования <br/>
 * {@link #UPDATE_STATUS_BOOKING} Изменить статус бронирования <br/>
 * {@link #GET_BOOKING}    Посмотреть бронирование <br/>
 * {@link #GET_ALL_BOOKINGS_FOR_USER}  Посмотреть бронирования от имени пользователя <br/>
 * {@link #GET_ALL_BOOKINGS_FOR_OWNER} Посмотреть бронирования от имени владельца предмета <br/>
 * <h3>Item Controller</h3>
 * {@link #CREATE_ITEM} Создать предмет <br/>
 * {@link #UPDATE_ITEM} Изменить предмет <br/>
 * {@link #GET_ITEM} Посмотреть предмет <br/>
 * {@link #SEARCH_ITEM} Поиск предмета <br/>
 * {@link #GET_ALL_ITEMS} Посмотреть все предметы <br/>
 * {@link #CREATE_COMMENT} Оставить комментарий для предмета <br/>
 * <h3>ItemRequest Controller</h3>
 * {@link #CREATE_REQUEST} Создать запрос на предмет <br/>
 * {@link #GET_BY_REQUESTER} Посмотреть запрос на предмет от имени запрашиваемого <br/>
 * {@link #GET_REQUEST} Посмотреть запрос пользователя <br/>
 * {@link #GET_ALL_REQUESTS} Посмотреть все запросы <br/>
 * <h3>User Controller</h3>
 * {@link #CREATE_USER} Создать пользователя <br/>
 * {@link #UPDATE_USER} Изменить пользователя <br/>
 * {@link #GET_USER}   Посмотреть пользователя <br/>
 * {@link #DELETE_USER} Удалить пользователя <br/>
 * {@link #GET_ALL_USERS} Посмотреть всех пользователей <br/>
 */
public interface Constants {
    String FROM = "0";
    String SIZE = "10";

    String HEADER_USER_ID = "X-Sharer-User-Id";

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
