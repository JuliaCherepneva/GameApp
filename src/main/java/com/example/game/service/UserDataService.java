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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

import static com.example.game.exception.ActivityLimitExceededException.LIMIT_ACTIVITY;
import static com.example.game.exception.InvalidJsonException.INVALID_JSON_FORMAT;
import static com.example.game.exception.InvalidJsonException.JSON_PROCESSING_ERROR;
import static com.example.game.exception.SyncLimitExceededException.MESSAGE_RQ;
import static com.example.game.exception.SyncLimitExceededException.MESSAGE_SYNC;
import static com.example.game.exception.UserNotFoundException.USER_NOT_FOUND;

/**
 * Сервис для обработки и синхронизации данных пользователей.
 *  <p>
 *  Этот класс предоставляет методы для обработки синхронизации данных, получение данных пользователя
 *  и обработку игровой активности.
 *  </p>
 */
@Service
public class UserDataService {

    private final UserDataRepository userDataRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    private static final Logger log = LoggerFactory.getLogger(UserDataService.class);

    @Autowired
    public UserDataService(UserDataRepository userDataRepository, Clock clock, ObjectMapper objectMapper) {
        this.userDataRepository = userDataRepository;
        this.clock = clock;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Обработка и синхронизация данных пользователя.
     *
     * @param uuid      UUID пользователя для синхронизации.
     * @param jsonData  Данные для синхронизации в формате JSON.
     * @return Сообщение об успешной синхронизации данных.
     * @throws IllegalArgumentException Если параметры "uuid" или "jsonData" пустые или некорректные.
     * @throws UserNotFoundException Если пользователь с данным UUID не найден.
     * @throws SyncLimitExceededException Если превышен лимит синхронизаций для пользователя.
     * @throws InvalidJsonException Если данные в формате JSON некорректны.
     */
    @CachePut(value = "users", key = "#uuid")
    public String processSyncData(String uuid, String jsonData) {
        long currentTime = Instant.now().toEpochMilli();
        log.info("Processing sync data for user: {}", uuid);

        UserData userData = userDataRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (userData.getSyncCount() >= 100) {
            throw new SyncLimitExceededException(MESSAGE_SYNC);
        }

        checkAndResetCounters(userData, false, currentTime);

        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            int money = rootNode.path("money").asInt();
            String country = rootNode.path("country").asText();

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

    /**
     * Получение данных пользователя по UUID.
     *
     * @param uuid UUID пользователя для получения данных.
     * @return Данные пользователя в формате JSON.
     * @throws UserNotFoundException Если пользователь с данным UUID не найден.
     * @throws SyncLimitExceededException Если превышен лимит запросов.
     * @throws InvalidJsonException Если возникла ошибка при сериализации данных.
     */
    @Cacheable(value = "users", key = "#uuid")
    public String getUserData(String uuid) {
        long currentTime = Instant.now(clock).toEpochMilli();
        log.info("Fetching user data for UUID: {}", uuid);

        UserData userData = userDataRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        checkAndResetCounters(userData, false, currentTime);

        if (userData.getSyncCount() >= 1) {
            throw new SyncLimitExceededException(MESSAGE_RQ);
        }

        userData.setSyncCount(userData.getSyncCount() + 1);
        userData.setLastSyncTime(currentTime);
        userDataRepository.save(userData);

        try {
            String jsonResult = objectMapper.writeValueAsString(userData);
            log.info("Serialized userData: {}", jsonResult);
            return jsonResult;

        } catch (JsonProcessingException e) {
            log.error("Error serializing user data for UUID: {}", uuid, e);
            throw new InvalidJsonException(JSON_PROCESSING_ERROR);
        }
    }

    /**
     * Обработка данных активности пользователя и обновление статистики.
     *
     * @param uuid уникальный идентификатор пользователя.
     * @param activity количество активности, которое нужно добавить к текущим данным пользователя.
     * @return сообщение об успешной обработке данных активности.
     * @throws UserNotFoundException если пользователь с заданным UUID не найден.
     * @throws ActivityLimitExceededException если лимит на количество запросов статистики превышен.
     */
    @CachePut(value = "users", key = "#uuid")
    public String processActivityData(String uuid, int activity) {
        long currentTime = Instant.now().toEpochMilli(); // Вычисляем время для текущего запроса
        log.info("Processing activity data for user: {}", uuid);

        UserData userData = userDataRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (userData.getStatCount() >= 10000) {
            throw new ActivityLimitExceededException(LIMIT_ACTIVITY);
        }

        checkAndResetCounters(userData, true, currentTime);

        userData.setActivity(userData.getActivity() + activity);
        userData.setStatCount(userData.getStatCount() + 1);
        userData.setLastStatTime(currentTime);
        userDataRepository.save(userData);

        return "Activity data received successfully.";
    }

    /**
     * Проверка, прошло ли более 24 часа с последнего обновления счетчиков статистики или синхронизации,
     * и сброс их в случае истечения этого времени.
     * <p>
     * В зависимости от типа проверки (статистика или синхронизация) метод сбрасывает счетчики активности
     * или синхронизации.
     * </p>
     *
     * @param userData данные пользователя, для которого выполняется проверка.
     * @param isStatCheck флаг, определяющий, нужно ли сбрасывать счетчик статистики (если true) или синхронизации (если false).
     * @param currentTime текущее время, использующееся для проверки истечения 24 часов.
     */
    private void checkAndResetCounters(UserData userData, boolean isStatCheck, long currentTime) {
        long timeDiff = currentTime - (isStatCheck ? userData.getLastStatTime() : userData.getLastSyncTime());

        if (timeDiff > 86400000) {
            if (isStatCheck) {
                userData.setStatCount(0);
                log.info("Reset stat count for user: {}", userData.getUuid());
            } else {
                userData.setSyncCount(0);
                log.info("Reset sync count for user: {}", userData.getUuid());
            }
        }
    }
}
