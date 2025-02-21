package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class SyncLimitExceededException extends RuntimeException {
    public static final String MESSAGE_SYNC = "Sync limit exceeded for today.";
    public static final String MESSAGE_RQ = "You have already received your data today.";

    public SyncLimitExceededException(String message) {
        super(message);
    }
}
