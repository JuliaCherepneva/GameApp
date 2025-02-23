package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, которое выбрасывается при отсутствии данных.
 * <p>
 * Этот класс расширяет {@link RuntimeException} и автоматически вызывает ошибку с кодом статуса 404 (NOT_FOUND) при возникновении исключения.
 * </p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoDataFoundException extends RuntimeException {

    /**
     * Константа определяющая сообщение при отсутствии истории активности пользователя.
     */
    public static final String ERROR_NO_ACTIVITY_FOR_USER = "No activity history found for user: ";

    /**
     * Константа определяющая сообщение при отсутствии пользователей для запрашиваемой страны.
     */
    public static final String ERROR_NO_ACTIVITY_FOR_COUNTRY = "No users found for country: ";

    /**
     * Конструктор исключения {@link NoDataFoundException}.
     * Создаёт новое исключение с сообщением.
     */
    public NoDataFoundException(String message) {
        super(message);
    }
}
