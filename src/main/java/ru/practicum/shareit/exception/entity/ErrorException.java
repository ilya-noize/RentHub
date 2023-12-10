package ru.practicum.shareit.exception.entity;

import lombok.Data;

@Data
public class ErrorException {

    private final int status;

    private final String error;
}
