package com.example.game.controller;

import com.example.game.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserDataController {
    private final UserDataService userDataService;
    private final Logger log = LoggerFactory.getLogger(UserDataController.class);

    // Эндпоинт для приема данных синхронизации
    @PostMapping("/sync")
    public String syncUserData(@RequestParam String uuid, @RequestBody String jsonData) {
        log.info("Syncing user data for uuid: {}", uuid);
        return userDataService.processSyncData(uuid, jsonData);
    }

    // Эндпоинт для отправки данных пользователю
    @GetMapping("/user-data")
    public String getUserData(@RequestParam String uuid) {
        log.info("Fetching user data for uuid: {}", uuid);
        return userDataService.getUserData(uuid);
    }

    // Эндпоинт для приема игровой статистики
    @PostMapping("/activity")
    public String processActivity(@RequestParam String uuid, @RequestParam int activity) {
        log.info("Processing activity for uuid: {}, activity: {}", uuid, activity);
        return userDataService.processActivityData(uuid, activity);
    }

}
