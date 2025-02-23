package com.example.game;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения для рекомендаций, который запускает Spring Boot приложение.
 * <p>
 * Аннотирован:
 * <br>- {@link SpringBootApplication} для конфигурации Spring Boot приложения.
 * <br>- {@link OpenAPIDefinition} для описания API с использованием OpenAPI.
 * <br>Этот класс является точкой входа в приложение.
 * </p>
 */
@SpringBootApplication
@OpenAPIDefinition
public class GameApplication {

	/**
	 * Точка входа в приложение. Запускает Spring Boot приложение.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);
	}

}
