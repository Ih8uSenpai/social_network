package com.example.social_network.dto;

import com.example.social_network.entity.Profile;
import lombok.Data;

@Data
public class PostData {

    private Long id;
    private Profile profile;
    private String content;
    private String createdAt;
    private int likesCount;
    private int sharesCount;
    private int commentsCount;
    private String mediaUrl;
    private String mediaType;
    private boolean userLiked;

}

