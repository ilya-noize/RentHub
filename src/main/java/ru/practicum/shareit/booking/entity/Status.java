package ru.practicum.shareit.booking.entity;

/**
 * Статус бронирования
 * <p>
 * WAITING — новое бронирование, ожидает одобрения,<br/>
 * APPROVED — бронирование подтверждено владельцем,<br/>
 * REJECTED — бронирование отклонено владельцем,<br/>
 * CANCELED — бронирование отменено создателем.
 */
public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
