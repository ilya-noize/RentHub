package ru.practicum.shareit.exception;

public class StateException extends RuntimeException {
    public StateException(String message) {
        super("Unknown state: " + message);
    }
}
