package ru.practicum.shareit.booking.entity;

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
public enum BookingFilterByTemplate {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}
