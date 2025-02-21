package com.example.game.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR = "error";

    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return Collections.singletonMap(ERROR, ex.getMessage());
    }

    @ExceptionHandler(InvalidJsonException.class)
    public Map<String, String> handleInvalidJsonException(InvalidJsonException ex) {
        log.warn("Invalid JSON input: {}", ex.getMessage());
        return Collections.singletonMap(ERROR, ex.getMessage());
    }

    @ExceptionHandler(SyncLimitExceededException.class)
    public Map<String, String> handleSyncLimitExceededException(SyncLimitExceededException ex) {
        log.warn("Sync limit exceeded: {}", ex.getMessage());
        return Collections.singletonMap(ERROR, ex.getMessage());
    }

    @ExceptionHandler(ActivityLimitExceededException.class)
    public Map<String, String> handleActivityLimitExceededException(ActivityLimitExceededException ex) {
        log.warn("Activity limit exceeded: {}", ex.getMessage());
        return Collections.singletonMap(ERROR, ex.getMessage());
    }

    @ExceptionHandler(NoDataFoundException.class)
    public Map<String, String> handleNoDataFoundException(NoDataFoundException e) {
        log.warn("No data found: {}", e.getMessage());
        return Collections.singletonMap(ERROR, e.getMessage());
    }
    @ExceptionHandler(DatabaseException.class)
    public Map<String, String> handleDatabaseException(DatabaseException e) {
        log.error("Database error: {}", e.getMessage());
        return Collections.singletonMap(ERROR, "Database error occurred. Please try again later.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid input: {}", e.getMessage());
        return Collections.singletonMap(ERROR, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return Collections.singletonMap(ERROR, "An unexpected error occurred.");
    }
}
