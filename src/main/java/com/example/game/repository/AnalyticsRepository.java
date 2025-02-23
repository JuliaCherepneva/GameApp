package com.example.game.repository;

import com.example.game.model.UserData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для аналитических запросов к данным пользователей.
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<UserData, String> {

    /**
     * Получает список пользователей из указанной страны, отсортированный по убыванию количества денег.
     * Используется для определения топ-N пользователей по финансовым показателям в конкретной стране.
     *
     * @param country  Код страны.
     * @param pageable Объект для пагинации и ограничения количества записей.
     * @return Список пользователей, отсортированный по количеству денег.
     */
    @Query("SELECT u FROM UserData u WHERE u.country = :country ORDER BY u.money DESC")
    List<UserData> findTopUsersByCountryMoney(@Param("country") String country, Pageable pageable);

    /**
     * Подсчитывает количество новых пользователей, зарегистрированных в указанной стране с определенной даты.
     *
     * @param country       Код страны.
     * @param startDateTime Дата и время, начиная с которых нужно считать новых пользователей.
     * @return Количество новых пользователей в стране за заданный период.
     */
    @Query("SELECT COUNT(u) FROM UserData u WHERE u.country = :country AND u.createdAt >= :startDateTime")
    long countNewUsersByCountry(@Param("country") String country, @Param("startDateTime") LocalDateTime startDateTime);
}

