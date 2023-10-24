package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.ZonedDateTime;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    public static final ZonedDateTime NOW = ZonedDateTime.now();

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleThrowable(Throwable e) {
        String message = e.getMessage();
        log.error("[!] {}", message);

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

        log.error("[!] Field {}: {}", field, message);

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
        log.error("[!] {}", message);

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
        log.error("[!] {}", message);

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
        log.error("[!] {}", message);

        return ResponseEntity
                .status(CONFLICT)
                .lastModified(NOW)
                .header("Already Exists", message)
                .build();
    }
}
