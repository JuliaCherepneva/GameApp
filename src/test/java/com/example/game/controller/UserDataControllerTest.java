package com.example.game.controller;

import com.example.game.service.UserDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class UserDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserDataService userDataService;

    @InjectMocks
    private UserDataController userDataController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userDataController).build(); // Настроить MockMvc вручную
    }


    @Test
    void syncUserData_ShouldReturnOk() throws Exception {
        // Мокаем данные, которые вернет сервис
        String mockResponse = "Sync successful";

        when(userDataService.processSyncData("test-uuid", "{\"data\":\"value\"}")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/sync")
                        .param("uuid", "test-uuid")
                        .content("{\"data\":\"value\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }

    @Test
    void getUserData_ShouldReturnUserData() throws Exception {
        // Мокаем данные, которые вернет сервис
        String mockResponse = "User data for test-uuid";

        when(userDataService.getUserData("test-uuid")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/user-data")
                        .param("uuid", "test-uuid"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }

    @Test
    void processActivity_ShouldReturnActivityData() throws Exception {
        // Мокаем данные, которые вернет сервис
        String mockResponse = "Activity data processed for test-uuid";

        when(userDataService.processActivityData("test-uuid", 100)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/activity")
                        .param("uuid", "test-uuid")
                        .param("activity", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }
}