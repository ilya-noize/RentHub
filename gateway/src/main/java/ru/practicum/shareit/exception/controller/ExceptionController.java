package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.RentalPeriodException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.exception.entity.ErrorException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    private void logError(String message, Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTraceString = sw.toString().replace(", ", ",\n");

        log.error("[!] Received the status {} Error: {}\n{}", HttpStatus.BAD_REQUEST, message, stackTraceString);
    }

    @ExceptionHandler(RentalPeriodException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleRentalPeriodException(RentalPeriodException e) {
        String message = e.getMessage();

        logError(message, e);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorException(400, message));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        String message = e.getMessage();

        logError(message, e);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorException(400, message));
    }

    @ExceptionHandler(StateException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleStateException(StateException e) {
        String message = e.getMessage();

        logError(message, e);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorException(400, message));
    }
}
