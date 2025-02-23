package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, которое выбрасывается при отсутствии пользователя в базе данных.
 * <p>
 * Этот класс расширяет {@link RuntimeException} и автоматически вызывает ошибку с кодом статуса 404 (NOT_FOUND) при возникновении исключения.
 * </p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Константа определяющая сообщение при отсутствии пользователя в базе данных.
     */
    public static final String USER_NOT_FOUND = "User not found. Please register first.";

    /**
     * Конструктор исключения {@link UserNotFoundException}.
     * Создаёт новое исключение с сообщением.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
