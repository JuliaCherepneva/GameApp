package com.example.game.controller;

import com.example.game.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с данными пользователя.
 * <p>
 * Включает обработку синхронизации данных, получение данных пользователя
 * и обработку игровой активности.
 * </p>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserDataController {

    private final UserDataService userDataService;
    private final Logger log = LoggerFactory.getLogger(UserDataController.class);

    /**
     * Обрабатывает приём данных пользователя для синхронизации.
     * <br>Endpoint: POST /api/sync
     *
     * @param uuid     Уникальный идентификатор пользователя.
     * @param jsonData JSON-данные для синхронизации.
     * @return Строка с результатом синхронизации.
     */
    @PostMapping("/sync")
    public ResponseEntity<String> syncUserData(@RequestParam String uuid, @RequestBody String jsonData) {
        log.info("Syncing user data for uuid: {}", uuid);
        String syncData = userDataService.processSyncData(uuid, jsonData);
        return ResponseEntity.ok(syncData);
    }

    /**
     * Обрабатывает данные пользователя, изменяя счётчик синхронизации и время последней синхронизации данных.
     * <br>Endpoint: POST /api/user-data
     *
     * @param uuid Уникальный идентификатор пользователя.
     * @return JSON-строка с данными пользователя.
     */
    @PostMapping("/user-data")
    public ResponseEntity<String> getUserData(@RequestParam String uuid) {
        log.info("Fetching user data for uuid: {}", uuid);
        String userData = userDataService.getUserData(uuid);
        return ResponseEntity.ok(userData);
    }

    /**
     * Обрабатывает игровую активность пользователя.
     *
     * @param uuid     Уникальный идентификатор пользователя.
     * @param activity Значение активности пользователя.
     * @return Строка с результатом обработки активности.
     */
    @PostMapping("/activity")
    public ResponseEntity<String> processActivity(@RequestParam String uuid, @RequestParam int activity) {
        log.info("Processing activity for uuid: {}, activity: {}", uuid, activity);
        String activityData = userDataService.processActivityData(uuid, activity);
        return ResponseEntity.ok(activityData);
    }
}
