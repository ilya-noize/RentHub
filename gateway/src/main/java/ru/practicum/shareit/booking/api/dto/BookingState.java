package ru.practicum.shareit.booking.api.dto;

import ru.practicum.shareit.exception.StateException;

/**
 * Для фильтрации списка бронирования у пользователя:
 * <ul>
 *     <li>ALL - все записи</li>
 *     <li>CURRENT - текущие предметы в аренде</li>
 *     <li>PAST - прошлые бронирования</li>
 *     <li>FUTURE - будущие бронирования</li>
 *     <li>WAITING - бронирования в ожидании решения от владельца предмета</li>
 *     <li>REJECTED - отказы в аренде от владельца предмета</li>
 * </ul>
 */
public enum BookingState {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static BookingState from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return state;
            }
        }
        throw new StateException(stringState);
    }
}
