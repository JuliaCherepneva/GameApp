package com.example.game.controller;

import com.example.game.model.UserActivityHistory;
import com.example.game.model.UserData;
import com.example.game.repository.UserDataRepository;
import com.example.game.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private UserDataRepository userDataRepository;
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

        List<UserData> topUsers = List.of(userData);

        when(analyticsService.getTopUsersByMoneyPerCountry("US", 1)).thenReturn(topUsers);

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
        long newUsersCount = 10L;

        when(analyticsService.countNewUsersByCountry("US", LocalDate.of(2025, 2, 22))).thenReturn(newUsersCount);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/analytics/new-users-count")
                        .param("country", "US")
                        .param("startDate", "2025-02-22")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(10));

        verify(analyticsService).countNewUsersByCountry("US", LocalDate.of(2025, 2, 22));
    }

    @Test
    void getUserActivityHistory_ShouldReturnActivityHistory() throws Exception {
        UserData userData = new UserData();
        userData.setUuid("test-uuid");

        UserActivityHistory activityHistory = new UserActivityHistory();

        activityHistory.setUser(userData);
        activityHistory.setActivity(1);
        activityHistory.setActivityDate(LocalDate.of(2025, 2, 22));

        List<UserActivityHistory> activityHistoryList = List.of(activityHistory);
        when(userDataRepository.findByUuid("test-uuid")).thenReturn(userData);
        when(analyticsService.getUserActivityHistory(userData, LocalDate.of(2025, 2, 22))).thenReturn(activityHistoryList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/analytics/user-activity-history")
                        .param("uuid", "test-uuid")
                        .param("startDate", "2025-02-22")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user.uuid").value("test-uuid"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].activity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].activityDate").value("2025-02-22"));

        verify(analyticsService).getUserActivityHistory(userData, LocalDate.of(2025, 2, 22));
    }
}