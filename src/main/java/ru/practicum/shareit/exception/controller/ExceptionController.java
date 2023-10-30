package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

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

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleThrowable(Throwable e) {
        String message = e.getMessage();

        logError(INTERNAL_SERVER_ERROR, message, e);

        return ResponseEntity
                .internalServerError()
                .lastModified(NOW)
                .header(message)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();

        String field = Objects.requireNonNull(result.getFieldError())
                .getField();
        String message = result.getAllErrors().get(0).getDefaultMessage();

        logError(BAD_REQUEST, message, e);

        return ResponseEntity
                .badRequest()
                .lastModified(NOW)
                .header(field, message)
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
        String message = e.getMessage();

        logError(BAD_REQUEST, message, e);

        return ResponseEntity
                .badRequest()
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
                .notFound()
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
                .header("Already Exists", message)
                .build();
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<?> handleAccessException(AccessException e) {
        String message = e.getMessage();

        logError(NOT_FOUND, message, e);

        return ResponseEntity
                .notFound()
                .lastModified(NOW)
                .header("Access denied", message)
                .build();
    }
}
