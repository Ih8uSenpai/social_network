package com.example.social_network.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class StaticFilesController {
    private final String NGINX_UPLOAD_URL = "https://sc-nginx.onrender.com/uploads/";
    private final String LOCAL_DIRECTORY = "C:\\Users\\herme\\IdeaProjects\\social_network\\nginx\\uploads\\";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // === 1. Сохранение на локальный диск ===
            File localFile = new File(LOCAL_DIRECTORY + file.getOriginalFilename());

            // Логирование пути
            System.out.println("Путь для сохранения: " + localFile.getAbsolutePath());

            // Проверка существования папки
            if (!localFile.getParentFile().exists()) {
                System.out.println("Папка не существует. Создаём...");
                boolean created = localFile.getParentFile().mkdirs();
                if (created) {
                    System.out.println("Папка успешно создана.");
                } else {
                    System.out.println("Не удалось создать папку!");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Не удалось создать папку на локальном диске.");
                }
            }

            // Сохранение файла
            try (FileOutputStream fos = new FileOutputStream(localFile)) {
                fos.write(file.getBytes());
            }
            System.out.println("Файл успешно сохранен на локальный диск.");

            // === 2. Отправка на Nginx ===
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/octet-stream");

            byte[] fileBytes = file.getBytes();
            org.springframework.http.HttpEntity<byte[]> requestEntity =
                    new org.springframework.http.HttpEntity<>(fileBytes, headers);

            String fileUrl = NGINX_UPLOAD_URL + file.getOriginalFilename();
            restTemplate.put(fileUrl, requestEntity);

            return ResponseEntity.ok("Файл успешно загружен:\n"
                    + "1. Локально: " + localFile.getAbsolutePath() + "\n"
                    + "2. На сервер: " + fileUrl);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка загрузки файла: " + e.getMessage());
        }
    }
}
