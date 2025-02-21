package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidJsonException extends RuntimeException {
    public static final String INVALID_JSON_FORMAT = "Invalid JSON format.";
    public static final String JSON_PROCESSING_ERROR = "Error processing JSON";

    public InvalidJsonException(String message) {
        super(message);
    }
}
