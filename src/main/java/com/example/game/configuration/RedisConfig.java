package com.example.game.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Конфигурация Redis-кэша.
 * Включает кеширование и определяет параметры подключения к Redis.
 */
@Configuration
@EnableCaching
public class RedisConfig {
    /**
     * Фабрика подключений к Redis с использованием Lettuce.
     *
     * @return объект RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * Конфигурация кеширования Redis.
     * Определяет TTL записей, запрещает кеширование null-значений и настраивает сериализацию.
     *
     * @param objectMapper для сериализации данных в JSON
     * @return объект RedisCacheConfiguration
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration(ObjectMapper objectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(objectMapper)
                ));
    }
}
