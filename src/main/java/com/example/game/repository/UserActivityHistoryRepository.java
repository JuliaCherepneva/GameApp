package com.example.game.repository;

import com.example.game.model.UserActivityHistory;
import com.example.game.model.UserData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторий для работы с историей активности пользователей.
 */
@Repository
public interface UserActivityHistoryRepository extends JpaRepository<UserActivityHistory, Long> {

    /**
     * Получает список записей активности пользователя за определенный период.
     * <p>
     * Используется для анализа активности пользователя.
     *</p>
     *
     * @param user     Уникальный идентификатор пользователя.
     * @param startDate Начальная дата (включительно), с которой учитывать активность.
     * @param pageable  Объект для пагинации и ограничения количества записей.
     * @return Список записей активности пользователя, отсортированный по дате убывания.
     */
    @Query("SELECT uah FROM UserActivityHistory uah WHERE uah.user = :user AND uah.activityDate >= :startDate ORDER BY uah.activityDate DESC")
    List<UserActivityHistory> findUserActivityHistoryByUuidAndPeriod(@Param("user") UserData user, @Param("startDate") LocalDate startDate, Pageable pageable);
}
