package ru.practicum.shareit.exception;

/**
 * Throwing an exception in case of violation of uniqueness
 */
public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
