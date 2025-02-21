package com.example.game.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneOffset;

@Configuration
public class AppConfig {
    @Bean
    public Clock clock() {
        return Clock.system(ZoneOffset.UTC); // Возвращаем Clock с часовым поясом UTC
    }
}
