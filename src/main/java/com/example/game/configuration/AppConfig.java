package com.example.game.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneOffset;

/**
 * Конфигурационный класс приложения.
 * Определяет бины, используемые в приложении.
 */
@Configuration
public class AppConfig {

    /**
     * Бин для получения текущего времени с часовым поясом UTC.
     *
     * @return объект Clock, настроенный на UTC
     */
    @Bean
    public Clock clock() {
        return Clock.system(ZoneOffset.UTC);
    }
}
