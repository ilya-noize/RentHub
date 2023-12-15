package ru.practicum.shareit.booking.api.enums;

/**
 * Для списка бронирования у владельца:
 * <ul>
 *      <li>WAITING - ожидание подтверждения/отказа</li>
 *      <li>APPROVED - подтверждено владельцем</li>
 *      <li>REJECTED - отказано в аренде владельцем</li>
 *      <li>CANCELED - отменено пользователем</li>
 * </ul>
 */

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED
}
