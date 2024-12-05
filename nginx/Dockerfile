FROM nginx:latest

# Копируем вашу кастомную конфигурацию
COPY nginx.conf /etc/nginx/nginx.conf

# Копируем статические файлы
COPY ./uploads /usr/share/nginx/uploads

# Открываем порт
EXPOSE 80
