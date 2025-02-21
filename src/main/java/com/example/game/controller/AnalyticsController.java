package com.example.game.controller;

import com.example.game.model.UserActivityHistory;
import com.example.game.model.UserData;
import com.example.game.service.AnalyticsService;
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

// Аналитический контроллер
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    // Эндпоинт для получения users пользователей с наибольшим значением "money" по каждой стране
    @GetMapping("/top-users-by-money")
    public ResponseEntity<List<UserData>> getTopUsersByMoneyPerCountry(@RequestParam String country, @RequestParam int usersCount) {
        log.info("Fetching top {} users by money for country: {}", usersCount, country);
        List<UserData> topUsers = analyticsService.getTopUsersByMoneyPerCountry(country, usersCount);
        return ResponseEntity.ok(topUsers);
    }

    // Эндпоинт для подсчета новых пользователей по каждой стране за период времени
    @GetMapping("/new-users-count")
    public ResponseEntity<Long> countNewUsersByCountry(@RequestParam String country, @RequestParam LocalDate startDate) {
        log.info("Counting new users for country: {} from date: {}", country, startDate);
        long count = analyticsService.countNewUsersByCountry(country, startDate);
        return ResponseEntity.ok(count);
    }

    // Эндпоинт для получения отсортированного списка активности пользователя за период времени
    @GetMapping("/user-activity-history")
    public ResponseEntity<List<UserActivityHistory>> getUserActivityHistory(@RequestParam String uuid, @RequestParam LocalDate startDate) {
        log.info("Fetching activity history for user: {} from date: {}", uuid, startDate);
       List<UserActivityHistory> activityHistory = analyticsService.getUserActivityHistory(uuid, startDate);
        return ResponseEntity.ok(activityHistory);
    }
}
