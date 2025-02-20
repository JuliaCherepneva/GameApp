package com.example.game.repository;

import com.example.game.model.UserActivityHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserActivityHistoryRepository  extends JpaRepository<UserActivityHistory, Long> {
    // Получение активности пользователя за определенный период
    @Query("SELECT uah FROM UserActivityHistory uah WHERE uah.uuid = :uuid AND uah.activityDate >= :startDate ORDER BY uah.activityDate DESC")
    List<UserActivityHistory> findUserActivityHistoryByUuidAndPeriod(@Param("uuid") String uuid, @Param("startDate") LocalDate startDate, Pageable pageable);
}
