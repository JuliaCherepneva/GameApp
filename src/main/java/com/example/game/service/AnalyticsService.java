package com.example.game.service;

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

// Аналитический сервис
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    private final AnalyticsRepository analyticsRepository;
    private final UserActivityHistoryRepository userActivityHistoryRepository;

    // Получение пользователей с наибольшим значением "money" по каждой стране
    @Cacheable(value = "topUsers", key = "#country + '_' + #usersCount")
    public List<UserData> getTopUsersByMoneyPerCountry(String country, int usersCount) {
        log.info("Fetching top {} users by money for country: {}", usersCount, country);
        Pageable pageable = PageRequest.of(0, usersCount);
        return analyticsRepository.findTopUsersByCountryMoney(country, pageable);
    }

    // Подсчет новых пользователей по каждой стране за период X
    @Cacheable(value = "newUsersCount", key = "#country + '_' + #startDate")
    public long countNewUsersByCountry(String country, LocalDate startDate) {
        log.info("Counting new users for country: {} from date: {}", country, startDate);
        // Преобразуем LocalDate в LocalDateTime с временем 00:00:00
        LocalDateTime startDateTime = startDate.atStartOfDay();
        return analyticsRepository.countNewUsersByCountry(country, startDateTime);
    }

    // Получение отсортированного по дате списка значений активности пользователя
    @Cacheable(value = "userActivityHistory", key = "#uuid + '_' + #startDate")
    public List<UserActivityHistory> getUserActivityHistory(String uuid, LocalDate startDate) {
        log.info("Fetching activity history for user: {} from date: {}", uuid, startDate);
        Pageable pageable = PageRequest.of(0, 10000); // Ограничиваем 10000 запросами в день
        return userActivityHistoryRepository.findUserActivityHistoryByUuidAndPeriod(uuid, startDate, pageable);
    }
}
