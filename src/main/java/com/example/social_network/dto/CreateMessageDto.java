package com.example.social_network.dto;

import lombok.Data;

@Data
public class CreateMessageDto {
    private Long chatId;
    private String content;
    private Long senderId;
}
