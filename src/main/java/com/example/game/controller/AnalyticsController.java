package com.example.game.controller;

import com.example.game.model.UserActivityHistory;
import com.example.game.model.UserData;
import com.example.game.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для предоставления аналитических данных о пользователях.
 * <p>
 * Содержит эндпоинты для получения списка топ-пользователей по количеству денег,
 * подсчета новых пользователей в стране за период, а также истории активности пользователя.
 * </p>
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    /**
     * Получает список пользователей с наибольшим значением "money" по указанной стране.
     * <br>Endpoint: GET /api/analytics/top-users-by-money
     *
     * @param country    Код страны.
     * @param usersCount Количество пользователей для вывода.
     * @return Список пользователей, отсортированный по убыванию количества денег.
     */
    @Operation(tags = {"Аналитика"}, summary = "Получение списка топ-пользователей")
    @GetMapping("/top-users-by-money")
    public ResponseEntity<List<UserData>> getTopUsersByMoneyPerCountry(@RequestParam String country, @RequestParam int usersCount) {
        log.info("Fetching top {} users by money for country: {}", usersCount, country);
        List<UserData> topUsers = analyticsService.getTopUsersByMoneyPerCountry(country, usersCount);
        return ResponseEntity.ok(topUsers);
    }

    /**
     * Подсчитывает количество новых пользователей, зарегистрированных в указанной стране с определенной даты.
     * <br>Endpoint: GET /api/analytics/new-users-count
     *
     * @param country   Код страны.
     * @param startDate Дата начала периода, за который считается количество новых пользователей.
     * @return Количество новых пользователей в стране за заданный период.
     */
    @Operation(tags = {"Аналитика"}, summary = "Получение количества новых пользователей")
    @GetMapping("/new-users-count")
    public ResponseEntity<Long> countNewUsersByCountry(@RequestParam String country, @RequestParam LocalDate startDate) {
        log.info("Counting new users for country: {} from date: {}", country, startDate);
        long count = analyticsService.countNewUsersByCountry(country, startDate);
        return ResponseEntity.ok(count);
    }

    /**
     * Получает список активности пользователя за определенный период.
     * <br>Endpoint: GET /api/analytics/user-activity-history
     * <p>
     * Записи отсортированы по дате убывания.
     * </p>
     *
     * @param uuid      Уникальный идентификатор пользователя.
     * @param startDate Дата начала периода активности.
     * @return Список записей активности пользователя.
     */
    @Operation(tags = {"Аналитика"}, summary = "Получение истории активности пользователя")
    @GetMapping("/user-activity-history")
    public ResponseEntity<List<UserActivityHistory>> getUserActivityHistory(@RequestParam String uuid, @RequestParam LocalDate startDate) {
        log.info("Fetching activity history for user: {} from date: {}", uuid, startDate);
        List<UserActivityHistory> activityHistory = analyticsService.getUserActivityHistory(uuid, startDate);
        return ResponseEntity.ok(activityHistory);
    }
}
