package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, которое выбрасывается при ошибках с базой данных.
 * <p>
 * Этот класс расширяет {@link RuntimeException} и автоматически вызывает ошибку с кодом статуса 500 (INTERNAL_SERVER_ERROR) при возникновении исключения.
 * </p>
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseException extends RuntimeException {
    /**
     * Константа определяющая сообщение при неудаче в поиске лучших пользователей.
     */
    public static final String ERROR_TOP_USERS = "Failed to fetch top users. Please try again later";

    /**
     * Константа определяющая сообщение при неудаче получения истории активности.
     */
    public static final String ERROR_ACTIVITY_HISTORY = "Failed to fetch activity history. Please try again later";

    /**
     * Константа определяющая сообщение при неудаче получения количества новых пользователей.
     */
    public static final String ERROR_NEW_USERS = "Failed to count new users. Please try again later";

    /**
     * Конструктор исключения {@link DatabaseException}.
     * Создаёт новое исключение с сообщением.
     */
    public DatabaseException(String message) {
        super(message);
    }
}
