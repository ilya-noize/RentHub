package ru.practicum.shareit.exception.entity;

import lombok.Data;

@Data
public class ErrorException {
    private final int statusCode;
    private final String message;
}
