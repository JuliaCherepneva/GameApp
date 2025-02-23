package com.example.game.controller;

import com.example.game.service.UserDataService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
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
        String mockResponse = "User data for test-uuid";

        when(userDataService.getUserData("test-uuid")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/user-data")
                        .param("uuid", "test-uuid"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }

    @Test
    void processActivity_ShouldReturnActivityData() throws Exception {
        String mockResponse = "Activity data processed for test-uuid";

        when(userDataService.processActivityData("test-uuid", 100)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/activity")
                        .param("uuid", "test-uuid")
                        .param("activity", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }
}