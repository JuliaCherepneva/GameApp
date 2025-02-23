package com.example.game.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность, представляющая данные пользователя.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_data")
public class UserData {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    private String uuid;

    /**
     * Количество денег у пользователя.
     */
    private int money;

    /**
     * Код страны пользователя (3 символа, не может быть null).
     */
    @Column(length = 3, nullable = false)
    private String country;

    /**
     * Показатель пользовательской активности в игре.
     */
    private int activity;

    /**
     * Количество синхронизаций данных (ограничение: до 100 в день).
     */
    @Column(name = "sync_count", nullable = false)
    private int syncCount;

    /**
     * Количество обновлений статистики активности (ограничение: до 10000 в день).
     */
    @Column(name = "stat_count", nullable = false)
    private int statCount;

    /**
     * Время последней синхронизации данных в миллисекундах.
     */
    @Column(name = "last_sync_time")
    private long lastSyncTime;

    /**
     * Время последнего обновления активности в миллисекундах.
     */
    @Column(name = "last_stat_time")
    private long lastStatTime;

    /**
     * Дата и время регистрации пользователя (создание пользователя в БД).
     * Значение устанавливается автоматически при создании.
     * Не обновляется.
     */
    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime createdAt;

    /**
     * Список записей активности пользователя.
     * <p>
     * Отношение "один ко многим" (OneToMany) с сущностью {@link UserActivityHistory},
     * где каждая запись активности привязана к конкретному пользователю.
     * </p>
     * <p>
     * Связь управляется полем "user" в {@link UserActivityHistory}.
     * </p>
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivityHistory> activityHistory = new ArrayList<>();

    /**
     * Метод, вызываемый перед сохранением новой записи в БД (регистрацией пользователя).
     * Устанавливает текущую дату и время создания, если оно не задано.
     */
    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        }
    }
}
