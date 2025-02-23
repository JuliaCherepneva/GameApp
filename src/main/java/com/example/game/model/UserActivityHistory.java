package com.example.game.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Сущность, представляющая аналитические данные пользователя.
 */
@Schema(description = "Сущность, представляющая аналитические данные пользователя")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_activity_history")
public class UserActivityHistory {

    /**
     * Уникальный идентификатор записи.
     */
    @Schema(description = "Уникальный идентификатор записи")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, к которому относится данная запись активности.
     * <p>
     * Отношение "многие к одному" (ManyToOne) с сущностью {@link UserData},
     * где множество записей активности могут быть привязаны к одному пользователю.
     * </p>
     * <p>
     * Поле "uuid" в таблице <b>user_activity_history</b> является внешним ключом,
     * ссылающимся на "uuid" в таблице <b>user_data</b>.
     * </p>
     */
    @Schema(description = "Пользователь, к которому относится данная запись активности")
    @ManyToOne
    @JoinColumn(name = "uuid", nullable = false)
    private UserData user;

    /**
     * Показатель активности пользователя.
     */
    @Schema (description = "Показатель активности пользователя")
    @Column(nullable = false)
    private Integer activity;

    /**
     * Дата активности пользователя для анализа статистики по дням.
     */
    @Schema (description = "Дата активности пользователя")
    @Column(name = "activity_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityDate;
}
