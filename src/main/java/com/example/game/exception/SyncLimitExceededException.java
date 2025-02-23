package com.example.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, которое выбрасывается при превышении лимита запросов на синхронизацию данных пользователя.
 * <p>
 * Этот класс расширяет {@link RuntimeException} и автоматически вызывает ошибку с кодом статуса 429 (TOO_MANY_REQUESTS) при возникновении исключения.
 * </p>
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class SyncLimitExceededException extends RuntimeException {

    /**
     * Константа определяющая сообщение при превышении лимита количества отправлений информации в день.
     */
    public static final String MESSAGE_SYNC = "Sync limit exceeded for today.";

    /**
     * Константа определяющая сообщение при превышении лимита получения информации в день.
     */
    public static final String MESSAGE_RQ = "You have already received your data today.";

    /**
     * Конструктор исключения {@link SyncLimitExceededException}.
     * <br>Создаёт новое исключение с сообщением.
     */
    public SyncLimitExceededException(String message) {
        super(message);
    }
}
