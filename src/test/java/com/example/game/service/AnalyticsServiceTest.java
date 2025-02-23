package com.example.game.service;

import com.example.game.exception.DatabaseException;
import com.example.game.model.UserActivityHistory;
import com.example.game.model.UserData;
import com.example.game.repository.AnalyticsRepository;
import com.example.game.repository.UserActivityHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {
    @InjectMocks
    private AnalyticsService analyticsService;
    @Mock
    private AnalyticsRepository analyticsRepository;
    @Mock
    private UserActivityHistoryRepository userActivityHistoryRepository;

    @Test
    void getTopUsersByMoneyPerCountry_ShouldReturnUsers() {
        String country = "US";
        int usersCount = 3;
        List<UserData> mockUsers = List.of(new UserData(), new UserData(), new UserData());

        when(analyticsRepository.findTopUsersByCountryMoney(eq(country), any(Pageable.class)))
                .thenReturn(mockUsers);

        List<UserData> result = analyticsService.getTopUsersByMoneyPerCountry(country, usersCount);

        assertThat(result).hasSize(usersCount);
        verify(analyticsRepository).findTopUsersByCountryMoney(eq(country), any(Pageable.class));
    }

    @Test
    void getTopUsersByMoneyPerCountry_ShouldThrowException_WhenUsersCountInvalid() {
        assertThatThrownBy(() -> analyticsService.getTopUsersByMoneyPerCountry("US", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The number of users must be at least 1.");
    }

    @Test
    void getTopUsersByMoneyPerCountry_ShouldThrowException_WhenCountryIsEmpty() {
        assertThatThrownBy(() -> analyticsService.getTopUsersByMoneyPerCountry("", 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Country must not be null or empty.");
    }

    @Test
    void countNewUsersByCountry_ShouldReturnCount() {
        String country = "US";
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        long expectedCount = 10L;

        when(analyticsRepository.countNewUsersByCountry(eq(country), any(LocalDateTime.class)))
                .thenReturn(expectedCount);

        long result = analyticsService.countNewUsersByCountry(country, startDate);

        assertThat(result).isEqualTo(expectedCount);
        verify(analyticsRepository).countNewUsersByCountry(eq(country), any(LocalDateTime.class));
    }

    @Test
    void countNewUsersByCountry_ShouldThrowException_WhenCountryIsEmpty() {
        assertThatThrownBy(() -> analyticsService.countNewUsersByCountry("", LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Country must not be null or empty.");
    }

    @Test
    void getUserActivityHistory_ShouldReturnHistory() {
        UserData userData = new UserData();
        userData.setUuid("test-uuid");
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        List<UserActivityHistory> mockHistory = List.of(new UserActivityHistory(), new UserActivityHistory());

        when(userActivityHistoryRepository.findUserActivityHistoryByUuidAndPeriod(eq(userData), eq(startDate), any(Pageable.class)))
                .thenReturn(mockHistory);

        List<UserActivityHistory> result = analyticsService.getUserActivityHistory(userData, startDate);

        assertThat(result).hasSize(mockHistory.size());
        verify(userActivityHistoryRepository).findUserActivityHistoryByUuidAndPeriod(eq(userData), eq(startDate), any(Pageable.class));
    }


    @Test
    void getUserActivityHistory_ShouldThrowException_WhenDatabaseErrorOccurs() {
        UserData userData = new UserData();
        userData.setUuid("valid-uuid");

        // Мокаем репозиторий, чтобы он вызвал исключение при обращении
        when(userActivityHistoryRepository.findUserActivityHistoryByUuidAndPeriod(eq(userData), any(LocalDate.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Проверяем, что выбрасывается DatabaseException, а не IllegalArgumentException
        assertThatThrownBy(() -> analyticsService.getUserActivityHistory(userData, LocalDate.now()))
                .isInstanceOf(DatabaseException.class)
                .hasMessage("Failed to fetch activity history. Please try again later");
    }
}