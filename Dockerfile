# Этап 1: Скачивание герба
FROM ubuntu:20.04 AS download_herb

# Устанавливаем wget
RUN apt-get update && apt-get install -y wget && apt-get clean

# Скачиваем изображение герба
RUN wget https://www.mirea.ru/upload/medialibrary/80f/MIREA_Gerb_Colour.png -P /app/uploads

# Этап 2: Сборка проекта
FROM maven:3.8-openjdk-18 AS builder

# Указываем рабочую директорию для сборки
WORKDIR /app

# Копируем исходный код проекта
COPY ../.. .

# Копируем герб из предыдущего этапа
COPY --from=download_herb /app/src/main/resources/static/MIREA_Gerb_Colour.png /app/uploads

# Собираем проект
RUN mvn clean package -DskipTests

# Этап 3: Запуск приложения
FROM openjdk:18

# Указываем рабочую директорию для запуска
WORKDIR /app

# Копируем собранный JAR файл из предыдущего этапа
COPY --from=builder /app/target/social_network-0.0.1-SNAPSHOT.jar app.jar

# Устанавливаем переменные окружения для работы с базой данных и фронтендом
ENV DB_PORT=5432


# Указываем информацию о контейнере
LABEL maintainer="Маров Егор Андреевич" group="ИКБО-24-21"

# Экспонируем порты
EXPOSE 8080

# Указываем точку монтирования для тома
VOLUME ["/app/data"]


# Указываем точку входа
ENTRYPOINT ["java", "-jar", "app.jar"]

# Включаем команду для выполнения при завершении сборки
ONBUILD RUN echo "Сборка и запуск произведены. Автор: Маров Егор Андреевич"
