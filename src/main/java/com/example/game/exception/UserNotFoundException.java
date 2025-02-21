package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public static final String USER_NOT_FOUND = "User not found. Please register first.";
    public UserNotFoundException(String message) {
        super(message);
    }
}
