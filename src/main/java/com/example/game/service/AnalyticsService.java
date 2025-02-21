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


// Аналитический сервис
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    private final AnalyticsRepository analyticsRepository;
    private final UserActivityHistoryRepository userActivityHistoryRepository;
    public static final String COUNTRY_REQUIRED = "Country must not be null or empty.";

    // Получение пользователей с наибольшим значением "money" по каждой стране
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

    // Подсчет новых пользователей по каждой стране за период X
    @Cacheable(value = "newUsersCount", key = "#country + '_' + #startDate")
    public long countNewUsersByCountry(String country, LocalDate startDate) {
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException(COUNTRY_REQUIRED);
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date must not be null.");
        }

        log.info("Counting new users for country: {} from date: {}", country, startDate);
        LocalDateTime startDateTime = startDate.atStartOfDay(); // Преобразуем LocalDate в LocalDateTime с временем 00:00:00

        try {
            return analyticsRepository.countNewUsersByCountry(country, startDateTime);
        } catch (Exception e) {
            log.error("Error counting new users for country: {}", country, e);
            throw new DatabaseException(ERROR_NEW_USERS);
        }
    }

    // Получение отсортированного по дате списка значений активности пользователя
    @Cacheable(value = "userActivityHistory", key = "#uuid + '_' + #startDate")
    public List<UserActivityHistory> getUserActivityHistory(String uuid, LocalDate startDate) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("User UUID must not be null or empty.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date must not be null.");
        }

        log.info("Fetching activity history for user: {} from date: {}", uuid, startDate);
        Pageable pageable = PageRequest.of(0, 10000);// Ограничиваем 10000 запросами в день

        try {
            List<UserActivityHistory> history = userActivityHistoryRepository.findUserActivityHistoryByUuidAndPeriod(uuid, startDate, pageable);
            if (history.isEmpty()) {
                throw new NoDataFoundException(ERROR_NO_ACTIVITY_FOR_USER + uuid);
            }
            return history;
        } catch (Exception e) {
            log.error("Error fetching activity history for user: {}", uuid, e);
            throw new DatabaseException(ERROR_ACTIVITY_HISTORY);
        }
    }
}
