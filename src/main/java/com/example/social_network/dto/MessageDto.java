package com.example.social_network.dto;

import com.example.social_network.entity.Chat;
import com.example.social_network.entity.Profile;
import com.example.social_network.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long messageId;
    private Profile sender;
    private Chat chat;
    private String content;
    private LocalDateTime sentAt;
    private boolean isViewed = false;
    private boolean isSingle = true;
}
