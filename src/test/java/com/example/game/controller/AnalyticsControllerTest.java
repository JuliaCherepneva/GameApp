package com.example.game.controller;

import com.example.game.model.UserActivityHistory;
import com.example.game.model.UserData;
import com.example.game.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController).build();
    }

    @Test
    void getTopUsersByMoneyPerCountry_ShouldReturnUsers() throws Exception {
        UserData userData = new UserData();
        userData.setUuid("test-uuid");
        userData.setCountry("US");
        userData.setMoney(1000);

        // Создание списка с одним элементом UserData
        List<UserData> topUsers = List.of(userData);

        // Настройка мок-сервиса
        when(analyticsService.getTopUsersByMoneyPerCountry("US", 1)).thenReturn(topUsers);

        // Выполнение запроса и проверка результатов
        mockMvc.perform(MockMvcRequestBuilders.get("/api/analytics/top-users-by-money")
                        .param("country", "US")
                        .param("usersCount", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].uuid").value("test-uuid"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].country").value("US"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].money").value(1000));

        verify(analyticsService).getTopUsersByMoneyPerCountry("US", 1); // Проверяем, что метод был вызван
    }

    @Test
    void countNewUsersByCountry_ShouldReturnCount() throws Exception {
        // Мокаем данные, которые вернет сервис
        long newUsersCount = 10L;

        // Настройка мок-сервиса
        when(analyticsService.countNewUsersByCountry("US", LocalDate.of(2025, 2, 22))).thenReturn(newUsersCount);

        // Выполнение запроса и проверка результатов
        mockMvc.perform(MockMvcRequestBuilders.get("/api/analytics/new-users-count")
                        .param("country", "US")
                        .param("startDate", "2025-02-22")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(10));

        verify(analyticsService).countNewUsersByCountry("US", LocalDate.of(2025, 2, 22)); // Проверяем, что метод был вызван
    }

    @Test
    void getUserActivityHistory_ShouldReturnActivityHistory() throws Exception {
        // Мокаем данные, которые вернет сервис
        UserActivityHistory activityHistory = new UserActivityHistory();
        activityHistory.setUuid("test-uuid");
        activityHistory.setActivity(1); // Пример значения активности
        activityHistory.setActivityDate(LocalDate.of(2025, 2, 22)); // Устанавливаем нужную дату

        // Создание списка с одним элементом UserActivityHistory
        List<UserActivityHistory> activityHistoryList = List.of(activityHistory);

        // Настройка мок-сервиса
        when(analyticsService.getUserActivityHistory("test-uuid", LocalDate.of(2025, 2, 22))).thenReturn(activityHistoryList);

        // Выполнение запроса и проверка результатов
        mockMvc.perform(MockMvcRequestBuilders.get("/api/analytics/user-activity-history")
                        .param("uuid", "test-uuid")
                        .param("startDate", "2025-02-22") // Используем строку даты вместо LocalDate.now().toString()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].uuid").value("test-uuid"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].activity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].activityDate").value("2025-02-22")); // Сравниваем как строку

        verify(analyticsService).getUserActivityHistory("test-uuid", LocalDate.of(2025, 2, 22)); // Проверяем, что метод был вызван
    }

}