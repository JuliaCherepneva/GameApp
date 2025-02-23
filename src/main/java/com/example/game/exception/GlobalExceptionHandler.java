package com.example.game.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

/**
 * Глобальный обработчик исключений в приложении.
 * Перехватывает и обрабатывает различные типы исключений, возвращая понятные сообщения об ошибках.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR = "error";

    /**
     * Обрабатывает исключение UserNotFoundException.
     *
     * @param e исключение о ненайденном пользователе
     * @return сообщение об ошибке
     */
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("User not found: {}", e.getMessage());
        return Collections.singletonMap(ERROR, e.getMessage());
    }

    /**
     * Обрабатывает исключение InvalidJsonException.
     *
     * @param e исключение о некорректном JSON-входе
     * @return сообщение об ошибке
     */
    @ExceptionHandler(InvalidJsonException.class)
    public Map<String, String> handleInvalidJsonException(InvalidJsonException e) {
        log.warn("Invalid JSON input: {}", e.getMessage());
        return Collections.singletonMap(ERROR, e.getMessage());
    }

    /**
     * Обрабатывает исключение SyncLimitExceededException.
     *
     * @param e исключение о превышении лимита синхронизации
     * @return сообщение об ошибке
     */
    @ExceptionHandler(SyncLimitExceededException.class)
    public Map<String, String> handleSyncLimitExceededException(SyncLimitExceededException e) {
        log.warn("Sync limit exceeded: {}", e.getMessage());
        return Collections.singletonMap(ERROR, e.getMessage());
    }

    /**
     * Обрабатывает исключение ActivityLimitExceededException.
     *
     * @param e исключение о превышении лимита запросов активности
     * @return сообщение об ошибке
     */
    @ExceptionHandler(ActivityLimitExceededException.class)
    public Map<String, String> handleActivityLimitExceededException(ActivityLimitExceededException e) {
        log.warn("Activity limit exceeded: {}", e.getMessage());
        return Collections.singletonMap(ERROR, e.getMessage());
    }

    /**
     * Обрабатывает исключение NoDataFoundException.
     *
     * @param e исключение о ненайденных данных
     * @return сообщение об ошибке
     */
    @ExceptionHandler(NoDataFoundException.class)
    public Map<String, String> handleNoDataFoundException(NoDataFoundException e) {
        log.warn("No data found: {}", e.getMessage());
        return Collections.singletonMap(ERROR, e.getMessage());
    }

    /**
     * Обрабатывает исключение DatabaseException.
     *
     * @param e исключение, связанное с ошибкой базы данных
     * @return сообщение об ошибке
     */
    @ExceptionHandler(DatabaseException.class)
    public Map<String, String> handleDatabaseException(DatabaseException e) {
        log.error("Database error: {}", e.getMessage());
        return Collections.singletonMap(ERROR, "Database error occurred. Please try again later.");
    }

    /**
     * Обрабатывает исключение IllegalArgumentException.
     *
     * @param e исключение о некорректном аргументе
     * @return сообщение об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid input: {}", e.getMessage());
        return Collections.singletonMap(ERROR, e.getMessage());
    }

    /**
     * Обрабатывает все непредвиденные исключения.
     *
     * @param e общее исключение
     * @return сообщение об ошибке
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGenericException(Exception e) {
        log.error("Unexpected error: ", e);
        return Collections.singletonMap(ERROR, "An unexpected error occurred.");
    }
}
