package ru.practicum.shareit.constants;

/**
 * <h1>GLOBAL VARIABLES - GATEWAY</h1>
 * <h2>Logs in tests</h2>
 * {@link #FROM} Константа pageable <br/>
 * {@link #SIZE} Константа pageable <br/>
 * <h2>Headers</h2>
 * {@link #HEADER_USER_ID} Имя заголовка для userId <br/>
 */
public interface Constants {
    String FROM = "0";
    String SIZE = "10";

    String HEADER_USER_ID = "X-Sharer-User-Id";
}
