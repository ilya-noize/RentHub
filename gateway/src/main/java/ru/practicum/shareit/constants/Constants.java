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

    String CREATE_BOOKING = "";
    String UPDATE_STATUS_BOOKING = "/{id}";
    String GET_BOOKING = "/{id}";
    String GET_ALL_BOOKINGS_FOR_USER = "";
    String GET_ALL_BOOKINGS_FOR_OWNER = "/owner";

    String CREATE_ITEM = "";
    String UPDATE_ITEM = "/{id}";
    String GET_ITEM = "/{id}";
    String SEARCH_ITEM = "/search";
    String GET_ALL_ITEMS = "";
    String CREATE_COMMENT = "/{id}/comment";

    String CREATE_REQUEST = "";
    String GET_BY_REQUESTER = "";
    String GET_REQUEST = "/{id}";
    String GET_ALL_REQUESTS = "/all";

    String CREATE_USER = "";
    String UPDATE_USER = "/{id}";
    String GET_USER = "/{id}";
    String DELETE_USER = "/{id}";
    String GET_ALL_USERS = "";
}
