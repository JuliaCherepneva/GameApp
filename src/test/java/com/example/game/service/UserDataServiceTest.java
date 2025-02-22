package com.example.game.service;

import com.example.game.exception.ActivityLimitExceededException;
import com.example.game.exception.InvalidJsonException;
import com.example.game.exception.SyncLimitExceededException;
import com.example.game.exception.UserNotFoundException;
import com.example.game.model.UserData;
import com.example.game.repository.UserDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import java.time.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class UserDataServiceTest {
    @InjectMocks
    private UserDataService userDataService;

    @Mock
    private UserDataRepository userDataRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Clock clock;


    @Test
    void processSyncData_ShouldUpdateUserData() throws Exception {
        String uuid = "test-uuid";
        String jsonData = "{\"money\":100, \"country\":\"US\"}";
        UserData userData = new UserData();
        userData.setUuid(uuid);
        userData.setSyncCount(0);

        when(userDataRepository.findById(uuid)).thenReturn(Optional.of(userData));

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(jsonData)).thenReturn(mockJsonNode);
        when(mockJsonNode.path("money")).thenReturn(new IntNode(100));
        when(mockJsonNode.path("country")).thenReturn(new TextNode("US"));

        String result = userDataService.processSyncData(uuid, jsonData);

        assertThat(result).isEqualTo("Data received successfully.");
        assertThat(userData.getSyncCount()).isEqualTo(1);

        verify(userDataRepository).findById(uuid);
        verify(userDataRepository).save(userData);
    }

    @Test
    void processSyncData_ShouldThrowUserNotFoundException() {
        String uuid = "unknown-uuid";
        String jsonData = "{\"money\":100, \"country\":\"US\"}";

        when(userDataRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDataService.processSyncData(uuid, jsonData))
                .isInstanceOf(UserNotFoundException.class);

        verify(userDataRepository).findById(uuid);
        verify(userDataRepository, never()).save(any());
    }

    @Test
    void processSyncData_ShouldThrowSyncLimitExceededException() {
        String uuid = "test-uuid";
        String jsonData = "{\"money\":100, \"country\":\"US\"}";
        UserData userData = new UserData();
        userData.setUuid(uuid);
        userData.setSyncCount(100); // Превышен лимит

        when(userDataRepository.findById(uuid)).thenReturn(Optional.of(userData));

        assertThatThrownBy(() -> userDataService.processSyncData(uuid, jsonData))
                .isInstanceOf(SyncLimitExceededException.class);

        verify(userDataRepository).findById(uuid);
        verify(userDataRepository, never()).save(any());
    }

    @Test
    void processSyncData_ShouldThrowInvalidJsonException() throws Exception {
        String uuid = "test-uuid";
        String invalidJson = "{money:100, country:US}"; // Некорректный JSON
        UserData userData = new UserData();
        userData.setUuid(uuid);

        when(userDataRepository.findById(uuid)).thenReturn(Optional.of(userData));

        when(objectMapper.readTree(invalidJson)).thenThrow(new JsonProcessingException("Invalid JSON") {
        });

        assertThatThrownBy(() -> userDataService.processSyncData(uuid, invalidJson))
                .isInstanceOf(InvalidJsonException.class);

        verify(userDataRepository).findById(uuid);
        verify(userDataRepository, never()).save(any());
    }

    @Test
    void getUserData_ShouldReturnUserDataAsJson() throws Exception {
        // Ожидаемое фиксированное время в UTC
        String expectedCreatedAt = "2025-02-22T08:54:13";
        LocalDateTime expectedLocalDateTime = LocalDateTime.parse(expectedCreatedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Instant expectedInstant = expectedLocalDateTime.atZone(ZoneOffset.UTC).toInstant();
        long expectedLastSyncTime = expectedInstant.toEpochMilli();

        // Фиксируем Clock
        Clock fixedClock = Clock.fixed(expectedInstant, ZoneOffset.UTC);

        // Пересоздаем сервис с фиксированным Clock
        userDataService = new UserDataService(userDataRepository, fixedClock, objectMapper);

        // Подготовка данных пользователя
        String uuid = "test-uuid";
        UserData userData = new UserData();
        userData.setUuid(uuid);
        userData.setCountry("US");
        userData.setSyncCount(0);
        userData.setStatCount(0);
        userData.setLastStatTime(0);
        userData.setLastSyncTime(expectedLastSyncTime);
        userData.setCreatedAt(expectedLocalDateTime);

        // Ожидаемый JSON
        String jsonResponse = "{\"uuid\":\"test-uuid\",\"money\":0,\"country\":\"US\",\"activity\":0," +
                "\"syncCount\":1,\"statCount\":0,\"lastSyncTime\":" + expectedLastSyncTime + ",\"lastStatTime\":0," +
                "\"createdAt\":\"2025-02-22T08:54:13\"}";

        // Мокаем репозиторий и ObjectMapper
        when(userDataRepository.findById(uuid)).thenReturn(Optional.of(userData));
        when(objectMapper.writeValueAsString(any(UserData.class))).thenReturn(jsonResponse);

        // Выполняем тестируемый метод
        String result = userDataService.getUserData(uuid);

        // Логируем результат
        System.out.println("Serialized userData: " + result);

        // Проверяем результат
        assertThat(result).isEqualTo(jsonResponse);

        // Проверяем вызовы моков
        verify(userDataRepository).findById(uuid);
        verify(objectMapper).writeValueAsString(any(UserData.class));
    }


    @Test
    void getUserData_ShouldThrowUserNotFoundException() {
        String uuid = "unknown-uuid";

        when(userDataRepository.findById(uuid)).thenReturn(Optional.empty());

        // Устанавливаем фиксированное время
        Instant fixedInstant = Instant.parse("2025-02-22T08:54:13Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        // Создаем сервис с фиксированным clock
        UserDataService userDataServiceWithFixedClock = new UserDataService(userDataRepository, fixedClock, objectMapper);

        assertThatThrownBy(() -> userDataServiceWithFixedClock.getUserData(uuid))
                .isInstanceOf(UserNotFoundException.class);

        verify(userDataRepository).findById(uuid);
    }

    @Test
    void processActivityData_ShouldUpdateUserActivity() {
        String uuid = "test-uuid";
        int activity = 10;
        UserData userData = new UserData();
        userData.setUuid(uuid);
        userData.setStatCount(0);
        userData.setActivity(0);

        when(userDataRepository.findById(uuid)).thenReturn(Optional.of(userData));

        String result = userDataService.processActivityData(uuid, activity);

        assertThat(result).isEqualTo("Activity data received successfully.");
        assertThat(userData.getActivity()).isEqualTo(10);
        assertThat(userData.getStatCount()).isEqualTo(1);

        verify(userDataRepository).findById(uuid);
        verify(userDataRepository).save(userData);
    }

    @Test
    void processActivityData_ShouldThrowUserNotFoundException() {
        String uuid = "unknown-uuid";
        int activity = 10;

        when(userDataRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDataService.processActivityData(uuid, activity))
                .isInstanceOf(UserNotFoundException.class);

        verify(userDataRepository).findById(uuid);
        verify(userDataRepository, never()).save(any());
    }

    @Test
    void processActivityData_ShouldThrowActivityLimitExceededException() {
        String uuid = "test-uuid";
        int activity = 10;
        UserData userData = new UserData();
        userData.setUuid(uuid);
        userData.setStatCount(10000); // Превышен лимит

        when(userDataRepository.findById(uuid)).thenReturn(Optional.of(userData));

        assertThatThrownBy(() -> userDataService.processActivityData(uuid, activity))
                .isInstanceOf(ActivityLimitExceededException.class);

        verify(userDataRepository).findById(uuid);
        verify(userDataRepository, never()).save(any());
    }

}
