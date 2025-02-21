package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoDataFoundException extends RuntimeException {
    public static final String ERROR_NO_ACTIVITY_FOR_USER = "No activity history found for user: ";
    public static final String ERROR_NO_ACTIVITY_FOR_COUNTRY = "No users found for country: ";
    public NoDataFoundException(String message) {
        super(message);
    }
}
