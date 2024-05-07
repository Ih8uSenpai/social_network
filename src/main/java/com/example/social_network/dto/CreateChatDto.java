package com.example.social_network.dto;

import com.example.social_network.entity.Profile;
import com.example.social_network.utils.ChatType;
import lombok.Data;

import java.util.List;

@Data
public class CreateChatDto {
    private Long id;
    private String name;
    private List<Long> userIds;
    private ChatType chatType;
    private Profile profileData;
}
