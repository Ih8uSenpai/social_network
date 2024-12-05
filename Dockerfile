FROM docker/compose:latest

# Копируем все файлы проекта
WORKDIR /app
COPY . .

# Запускаем Docker Compose
CMD ["docker-compose", "up", "--build"]
