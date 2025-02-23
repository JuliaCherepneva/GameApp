package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, которое выбрасывается при превышении лимита запросов на отправку статистики активности пользователя.
 * <p>
 * Этот класс расширяет {@link RuntimeException} и автоматически вызывает ошибку с кодом статуса 429 (TOO_MANY_REQUESTS) при возникновении исключения.
 * </p>
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class ActivityLimitExceededException extends RuntimeException {

    /**
     * Константа определяющая сообщение при превышении лимита количества отправки информации в день.
     */
    public static final String LIMIT_ACTIVITY = "Activity limit exceeded for today.";

    /**
     * Конструктор исключения {@link ActivityLimitExceededException}.
     * <br>Создаёт новое исключение с сообщением.
     */
    public ActivityLimitExceededException(String message) {
        super(message);
    }
}
