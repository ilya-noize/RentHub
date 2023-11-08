package ru.practicum.shareit.booking.entity;

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
    REJECTED,
    CANCELED // todo: use this constant in booking! Cancel booking from user in updateStatusById.
}
