package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class ActivityLimitExceededException extends RuntimeException {
    public ActivityLimitExceededException(String message) {
        super(message);
    }
}
