package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.entity.ErrorException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

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

//    @ExceptionHandler
//    @ResponseStatus(INTERNAL_SERVER_ERROR)
//    public ResponseEntity<?> handleThrowable(Throwable e) {
//        String message = e.getMessage();
//
//        logError(INTERNAL_SERVER_ERROR, message, e);
//
//        return ResponseEntity
//                .status(INTERNAL_SERVER_ERROR)
//                .lastModified(NOW)
//                .header("Error message", message)
//                .build();
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();

        String field = Objects.requireNonNull(result.getFieldError())
                .getField();
        String message = result.getAllErrors().get(0).getDefaultMessage();

        logError(BAD_REQUEST, message, e);

        return ResponseEntity
                .status(BAD_REQUEST)
                .lastModified(NOW)
                .header(field, message)
                .build();
    }

    @ExceptionHandler(RentalPeriodException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleRentalPeriodException(RentalPeriodException e) {
        String message = e.getMessage();

        logError(INTERNAL_SERVER_ERROR, message, e);

        return ResponseEntity
                .status(BAD_REQUEST)
                .lastModified(NOW)
                .header("Rental period", message)
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
        String message = e.getMessage();

        logError(BAD_REQUEST, message, e);

        return ResponseEntity
                .status(BAD_REQUEST)
                .lastModified(NOW)
                .header("null", message)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        String message = e.getMessage();

        logError(NOT_FOUND, message, e);

        return ResponseEntity
                .status(NOT_FOUND)
                .lastModified(NOW)
                .header("Not Found", message)
                .build();
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(CONFLICT)
    public ResponseEntity<?> handleAlreadyExistsException(AlreadyExistsException e) {
        String message = e.getMessage();

        logError(CONFLICT, message, e);

        return ResponseEntity
                .status(CONFLICT)
                .lastModified(NOW)
                .header("Already exists", message)
                .build();
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleAccessException(AccessException e) {
        String message = e.getMessage();

        logError(BAD_REQUEST, message, e);

        return ResponseEntity
                .status(BAD_REQUEST)
                .lastModified(NOW)
                .header("Access denied", message)
                .build();
    }

    @ExceptionHandler(StateException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleStateException(StateException e) {
        String message = e.getMessage();

        logError(INTERNAL_SERVER_ERROR, message, e);

        return new ResponseEntity<>(
                new ErrorException(
                        INTERNAL_SERVER_ERROR.value(),
                        message),
                INTERNAL_SERVER_ERROR);
    }
}
