package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseException extends RuntimeException {
    public static final String ERROR_TOP_USERS = "Failed to fetch top users. Please try again later";
    public static final String ERROR_ACTIVITY_HISTORY = "Failed to fetch activity history. Please try again later";
    public static final String ERROR_NEW_USERS = "Failed to count new users. Please try again later";

    public DatabaseException(String message) {
        super(message);
    }
}
