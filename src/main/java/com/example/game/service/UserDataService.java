package com.example.game.service;


import com.example.game.exception.ActivityLimitExceededException;
import com.example.game.exception.InvalidJsonException;
import com.example.game.exception.SyncLimitExceededException;
import com.example.game.exception.UserNotFoundException;
import com.example.game.model.UserData;
import com.example.game.repository.UserDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.example.game.exception.ActivityLimitExceededException.LIMIT_ACTIVITY;
import static com.example.game.exception.InvalidJsonException.INVALID_JSON_FORMAT;
import static com.example.game.exception.InvalidJsonException.JSON_PROCESSING_ERROR;
import static com.example.game.exception.SyncLimitExceededException.MESSAGE_RQ;
import static com.example.game.exception.SyncLimitExceededException.MESSAGE_SYNC;
import static com.example.game.exception.UserNotFoundException.USER_NOT_FOUND;

@Service
public class UserDataService {
    private static final Logger log = LoggerFactory.getLogger(UserDataService.class);
    private final UserDataRepository userDataRepository;
    private final ObjectMapper objectMapper;

    public UserDataService(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @CachePut(value = "users", key = "#uuid")
    public String processSyncData(String uuid, String jsonData) {
        long currentTime = Instant.now().toEpochMilli(); // Вычисляем время для текущего запроса
        log.info("Processing sync data for user: {}", uuid);

        // Получаем данные пользователя по UUID
        UserData userData = userDataRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        // Проверяем и сбрасываем счетчик синхронизаций, если прошло больше 24 часов
        checkAndResetCounters(userData, false, currentTime);

        // Проверяем, не превышен ли лимит запросов (100 раз в день)
        if (userData.getSyncCount() >= 100) {
            throw new SyncLimitExceededException(MESSAGE_SYNC);
        }
        //Если лимит не превышен, выполняем далее Безопасный парсинг JSON
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            int money = rootNode.path("money").asInt();
            String country = rootNode.path("country").asText();

            // Обновляем данные
            userData.setMoney(money);
            userData.setCountry(country);
            userData.setSyncCount(userData.getSyncCount() + 1);
            userData.setLastSyncTime(currentTime);
            userDataRepository.save(userData);

            log.info("Sync data successfully processed for user: {}", uuid);
            return "Data received successfully.";
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON for user: {}", uuid, e);
            throw new InvalidJsonException(INVALID_JSON_FORMAT);
        }

    }

    @Cacheable(value = "users", key = "#uuid")
    public String getUserData(String uuid) {
        long currentTime = Instant.now().toEpochMilli(); // Вычисляем время для текущего запроса
        log.info("Fetching user data for UUID: {}", uuid);

        // Получаем данные пользователя по UUID
        UserData userData = userDataRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        // Проверяем и сбрасываем счетчик синхронизаций, если прошло больше 24 часов
        checkAndResetCounters(userData, false, currentTime);

        // Проверяем, не превышен ли лимит запросов (1 раз в день)
        if (userData.getSyncCount() >= 1) {
            throw new SyncLimitExceededException(MESSAGE_RQ);
        }

        userData.setSyncCount(userData.getSyncCount() + 1); // Увеличиваем счетчик синхронизаций
        userData.setLastSyncTime(currentTime); // Обновляем время последней синхронизации
        userDataRepository.save(userData); // Сохраняем обновленные данные в базе

        try {
            return objectMapper.writeValueAsString(userData); // Сериализуем объект UserData в строку JSON и возвращаем результат
        } catch (JsonProcessingException e) {
            log.error("Error serializing user data for UUID: {}", uuid, e);
            throw new InvalidJsonException(JSON_PROCESSING_ERROR);
        }
    }

    // Прием игровой статистики от пользователя
    @CachePut(value = "users", key = "#uuid")
    public String processActivityData(String uuid, int activity) {
        long currentTime = Instant.now().toEpochMilli(); // Вычисляем время для текущего запроса
        log.info("Processing activity data for user: {}", uuid);

        // Получаем данные пользователя по UUID
        UserData userData = userDataRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        // Проверяем и сбрасываем счетчик статистики, если прошло больше 24 часов
        checkAndResetCounters(userData, true, currentTime);

        // Проверяем, не превышен ли лимит запросов (10000 раз в день)
        if (userData.getStatCount() >= 10000) {
            throw new ActivityLimitExceededException(LIMIT_ACTIVITY);
        }
        // Обновляем статистику
        userData.setActivity(userData.getActivity() + activity);
        userData.setStatCount(userData.getStatCount() + 1);
        userData.setLastStatTime(currentTime);
        userDataRepository.save(userData);

        return "Activity data received successfully.";
    }

    private void checkAndResetCounters(UserData userData, boolean isStatCheck, long currentTime) {
        long timeDiff = currentTime - (isStatCheck ? userData.getLastStatTime() : userData.getLastSyncTime());

        // Проверяем, прошло ли 24 часа
        if (timeDiff > 86400000) {  // 86400000 миллисекунд = 24 часа
            if (isStatCheck) {
                userData.setStatCount(0);
                log.info("Reset stat count for user: {}", userData.getUuid());// Сбросить счетчик статистики
            } else {
                userData.setSyncCount(0); // Сбросить счетчик синхронизаций
                log.info("Reset sync count for user: {}", userData.getUuid());
            }
        }
    }
}
