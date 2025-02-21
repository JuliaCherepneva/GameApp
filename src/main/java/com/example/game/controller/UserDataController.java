package com.example.game.controller;

import com.example.game.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserDataController {
    private final UserDataService userDataService;
    private final Logger log = LoggerFactory.getLogger(UserDataController.class);

    // Эндпоинт для приема данных синхронизации
    @PostMapping("/sync")
    public ResponseEntity<String> syncUserData(@RequestParam String uuid, @RequestBody String jsonData) {
        log.info("Syncing user data for uuid: {}", uuid);
        String syncData = userDataService.processSyncData(uuid, jsonData);
        return ResponseEntity.ok(syncData);
    }

    // Эндпоинт для отправки данных пользователю
    @GetMapping("/user-data")
    public ResponseEntity<String> getUserData(@RequestParam String uuid) {
        log.info("Fetching user data for uuid: {}", uuid);
        String userData = userDataService.getUserData(uuid);
        return ResponseEntity.ok(userData);
    }

    // Эндпоинт для приема игровой статистики
    @PostMapping("/activity")
    public ResponseEntity<String> processActivity(@RequestParam String uuid, @RequestParam int activity) {
        log.info("Processing activity for uuid: {}, activity: {}", uuid, activity);
        String activityData = userDataService.processActivityData(uuid, activity);
        return ResponseEntity.ok(activityData);
    }
}
