package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, которое выбрасывается при ошибках сериализации объекта в строку JSON.
 * <p>
 * Этот класс расширяет {@link RuntimeException} и автоматически вызывает ошибку с кодом статуса 400 (BAD_REQUEST) при возникновении исключения.
 * </p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidJsonException extends RuntimeException {
    /**
     * Константа определяющая сообщение при получении неверного формата JSON.
     */
    public static final String INVALID_JSON_FORMAT = "Invalid JSON format.";

    /**
     * Константа определяющая сообщение при ошибке сериализации объекта в строку JSON.
     */
    public static final String JSON_PROCESSING_ERROR = "Error processing JSON";

    /**
     * Конструктор исключения {@link InvalidJsonException}.
     * Создаёт новое исключение с сообщением.
     */
    public InvalidJsonException(String message) {
        super(message);
    }
}
