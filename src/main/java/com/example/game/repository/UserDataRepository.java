package com.example.game.repository;

import com.example.game.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с данными пользователей.
 * <p>
 * Предоставляет базовые CRUD-операции через {@link JpaRepository}.
 * </p>
 */
@Repository
public interface UserDataRepository extends JpaRepository<UserData, String> {
    UserData findByUuid(String uuid);
}

