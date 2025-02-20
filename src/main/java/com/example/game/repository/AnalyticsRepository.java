package com.example.game.repository;

import com.example.game.model.UserData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<UserData, String>  {

    // Получение X пользователей с наибольшим значением money по каждой стране
    @Query("SELECT u FROM UserData u WHERE u.country = :country ORDER BY u.money DESC")
    List<UserData> findTopUsersByCountryMoney(@Param("country") String country, Pageable pageable);

    // Подсчет новых пользователей по стране за период
    @Query("SELECT COUNT(u) FROM UserData u WHERE u.country = :country AND u.createdAt >= :startDateTime")
    long countNewUsersByCountry(@Param("country") String country, @Param("startDateTime") LocalDateTime startDateTime);

}

