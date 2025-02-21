# Запуск проекта с Docker Compose

## 1. Настроить переменные окружения
Данные для настройки:
- POSTGRES_DB
- POSTGRES_USER
- POSTGRES_PASSWORD

Находятся в файле`.env`
## 2. Запустить контейнеры
Выполните команду:

docker-compose up -d
## 3. Проверить запущенные сервисы
docker ps

Приложение будет доступно на `http://localhost:8080`