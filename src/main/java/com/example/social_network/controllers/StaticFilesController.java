package com.example.social_network.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "${FRONTEND_URL}")
@RequiredArgsConstructor
@Slf4j
public class StaticFilesController {
    private final String NGINX_UPLOAD_URL = "https://sc-nginx.onrender.com/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Создаем HTTP-запрос с содержимым файла
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/octet-stream");

            byte[] fileBytes = file.getBytes();
            org.springframework.http.HttpEntity<byte[]> requestEntity =
                    new org.springframework.http.HttpEntity<>(fileBytes, headers);

            // Отправляем запрос PUT на Nginx
            String fileUrl = NGINX_UPLOAD_URL + file.getOriginalFilename();
            restTemplate.put(fileUrl, requestEntity);

            return ResponseEntity.ok("Файл успешно загружен: " + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка загрузки файла: " + e.getMessage());
        }
    }
}
