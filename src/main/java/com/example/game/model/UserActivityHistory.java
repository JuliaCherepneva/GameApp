package com.example.game.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_activity_history")
public class UserActivityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid; // Идентификатор пользователя

    @Column(nullable = false)
    private Integer activity; // Показатель активности пользователя

    @Column(name = "activity_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityDate; // Дата активности
}
