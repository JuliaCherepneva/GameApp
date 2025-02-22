package com.example.game.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_data")
public class UserData {

    @Id
    private String uuid;

    private int money;

    @Column(length = 3, nullable = false)
    private String country;

    private int activity;

    @Column(name = "sync_count", nullable = false)
    private int syncCount;// Счётчик синхронизаций (до 100 в день)

    @Column(name = "stat_count", nullable = false)
    private int statCount;      // Счётчик статистики активности (до 10000 в день)

    @Column(name = "last_sync_time")
    private long lastSyncTime;  // Время последней синхронизации

    @Column(name = "last_stat_time")
    private long lastStatTime;// Время последнего обновления активности

    @Column(name = "created_at", updatable = false)
   // @JsonSerialize(using = LocalDateTimeSerializer.class)
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime createdAt;

/*
    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(Clock.systemDefaultZone());// Автоматическая установка времени при создании
        }
    }

 */

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        }
    }
}
