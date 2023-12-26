package ru.practicum.shareit.exception;

public class RentalPeriodException extends RuntimeException {
    public RentalPeriodException() {
    }

    public RentalPeriodException(String message) {
        super(message);
    }

    public RentalPeriodException(String message, Throwable cause) {
        super(message, cause);
    }
}
