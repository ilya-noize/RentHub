package ru.practicum.shareit.booking.api.dto;

import java.util.Optional;

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

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
