package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.RentalPeriodException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.exception.entity.ErrorException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    public static final ZonedDateTime NOW = ZonedDateTime.now();

    private void logError(HttpStatus status, String message, Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTraceString = sw.toString().replace(", ", "\n");

        log.error("[!] Received the status {} Error: {}\n{}", status, message, stackTraceString);
    }

    @ExceptionHandler(RentalPeriodException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleRentalPeriodException(RentalPeriodException e) {
        String message = e.getMessage();

        logError(BAD_REQUEST, message, e);

        return ResponseEntity
                .status(BAD_REQUEST)
                .lastModified(NOW)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorException(400, message));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        String message = e.getMessage();

        logError(NOT_FOUND, message, e);

        return ResponseEntity
                .status(NOT_FOUND)
                .lastModified(NOW)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorException(404, message));
    }


    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        String error = e.getMessage();

        logError(BAD_REQUEST, error, e);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorException(400, error));
    }

    @ExceptionHandler(StateException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleStateException(StateException e) {
        String message = e.getMessage();

        logError(BAD_REQUEST, message, e);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorException(400, message));
    }

    @ExceptionHandler(BookingException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<?> handleBookingException(BookingException e) {
        String message = e.getMessage();

        logError(NOT_FOUND, message, e);

        return ResponseEntity
                .status(NOT_FOUND)
                .lastModified(NOW)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorException(404, message));
    }
}
