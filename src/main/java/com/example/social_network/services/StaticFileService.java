package com.example.social_network.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class StaticFileService {
    @Value("${NGINX_UPLOAD_URL}")
    private String NGINX_UPLOAD_URL;


    public String uploadFile(MultipartFile file) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            // Устанавливаем Content-Type файла
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", file.getContentType());

            byte[] fileBytes = file.getBytes();
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileBytes, headers);

            // Кодируем имя файла
            String encodedFilename = URLEncoder.encode(file.getOriginalFilename(), StandardCharsets.UTF_8);
            String fileUrl = NGINX_UPLOAD_URL + encodedFilename;

            // Отправляем запрос PUT
            restTemplate.put(fileUrl, requestEntity);

            // === 1. Сохранение на локальный диск ===
            String LOCAL_DIRECTORY = "C:\\Users\\herme\\IdeaProjects\\social_network\\nginx\\uploads\\";
            File localFile = new File(LOCAL_DIRECTORY + file.getOriginalFilename());

            // Сохранение файла
            try (FileOutputStream fos = new FileOutputStream(localFile)) {
                fos.write(file.getBytes());
            }
            return fileUrl;
        } catch (IOException e) {
            return null;
        }
    }

}
