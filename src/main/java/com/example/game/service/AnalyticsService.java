package com.example.game.service;

import com.example.game.exception.DatabaseException;
import com.example.game.exception.NoDataFoundException;
import com.example.game.model.UserActivityHistory;
import com.example.game.model.UserData;
import com.example.game.repository.AnalyticsRepository;
import com.example.game.repository.UserActivityHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.example.game.exception.DatabaseException.*;
import static com.example.game.exception.NoDataFoundException.*;


/**
 * Сервис для выполнения аналитических операций с данными пользователей.
 * <p>
 * Этот класс предоставляет методы для получения списка топ-пользователей по количеству денег,
 * подсчета новых пользователей в стране за период, а также истории активности пользователя.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final UserActivityHistoryRepository userActivityHistoryRepository;

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    /**
     * Константа определяющая сообщение при передаче нулевого или пустого значения в параметр.
     */
    public static final String COUNTRY_REQUIRED = "Country must not be null or empty.";

    /**
     * Получение списка пользователей с наибольшим значением "money" по каждой стране.
     *
     * @param country     Страна для поиска пользователей.
     * @param usersCount  Количество пользователей, которых необходимо получить.
     * @return Список пользователей, отсортированных по убыванию значения "money" в пределах указанной страны.
     * @throws IllegalArgumentException Если параметр "country" пустой или равен null, или если "usersCount" меньше 1.
     * @throws NoDataFoundException Если не найдено данных для указанной страны.
     * @throws DatabaseException Если произошла ошибка при запросе данных из базы данных.
     */
    @Cacheable(value = "topUsers", key = "#country + '_' + #usersCount")
    public List<UserData> getTopUsersByMoneyPerCountry(String country, int usersCount) {
        if (usersCount < 1) {
            throw new IllegalArgumentException("The number of users must be at least 1.");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException(COUNTRY_REQUIRED);
        }

        log.info("Fetching top {} users by money for country: {}", usersCount, country);
        Pageable pageable = PageRequest.of(0, usersCount);

        try {
            List<UserData> users = analyticsRepository.findTopUsersByCountryMoney(country, pageable);
            if (users.isEmpty()) {
                throw new NoDataFoundException(ERROR_NO_ACTIVITY_FOR_COUNTRY + country);
            }
            return users;
        } catch (Exception e) {
            log.error("Error fetching top users by money for country: {}", country, e);
            throw new DatabaseException(ERROR_TOP_USERS);
        }
    }

    /**
     * Подсчет количества новых пользователей по каждой стране за период.
     *
     * @param country     Страна для подсчета новых пользователей.
     * @param startDate   Дата начала периода для подсчета новых пользователей.
     * @return Количество новых пользователей, зарегистрированных в указанной стране с заданной даты.
     * @throws IllegalArgumentException Если параметры "country" или "startDate" пустые или равны null.
     * @throws DatabaseException Если произошла ошибка при запросе данных из базы данных.
     */
    @Cacheable(value = "newUsersCount", key = "#country + '_' + #startDate")
    public long countNewUsersByCountry(String country, LocalDate startDate) {
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException(COUNTRY_REQUIRED);
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date must not be null.");
        }

        log.info("Counting new users for country: {} from date: {}", country, startDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();

        try {
            return analyticsRepository.countNewUsersByCountry(country, startDateTime);
        } catch (Exception e) {
            log.error("Error counting new users for country: {}", country, e);
            throw new DatabaseException(ERROR_NEW_USERS);
        }
    }

    /**
     * Получение списка истории активности пользователя, отсортированного по дате.
     *
     * @param user        UUID пользователя, чью активность нужно получить.
     * @param startDate   Дата начала периода для получения истории активности.
     * @return Список объектов {@link UserActivityHistory}, представляющих активность пользователя за указанный период.
     * @throws IllegalArgumentException Если параметры "uuid" или "startDate" пустые или равны null.
     * @throws NoDataFoundException Если не найдено данных активности для указанного пользователя.
     * @throws DatabaseException Если произошла ошибка при запросе данных из базы данных.
     */
    @Cacheable(value = "userActivityHistory", key = "#uuid + '_' + #startDate")
    public List<UserActivityHistory> getUserActivityHistory(UserData user, LocalDate startDate) {
        if (user == null) {
            throw new IllegalArgumentException("User UUID must not be null.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date must not be null.");
        }

        log.info("Fetching activity history for user: {} from date: {}", user, startDate);
        Pageable pageable = PageRequest.of(0, 10000);

        try {
            List<UserActivityHistory> history = userActivityHistoryRepository.findUserActivityHistoryByUuidAndPeriod(user, startDate, pageable);
            if (history.isEmpty()) {
                throw new NoDataFoundException(ERROR_NO_ACTIVITY_FOR_USER + user);
            }
            return history;
        } catch (Exception e) {
            log.error("Error fetching activity history for user: {}", user, e);
            throw new DatabaseException(ERROR_ACTIVITY_HISTORY);
        }
    }
}
