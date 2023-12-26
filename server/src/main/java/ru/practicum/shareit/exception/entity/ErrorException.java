package ru.practicum.shareit.exception.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorException {

    @JsonIgnore
    private final int statusCode;

    @JsonProperty("error")
    @JsonAlias("message")
    private final String message;
}
