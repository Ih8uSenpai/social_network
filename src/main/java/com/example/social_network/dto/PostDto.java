package com.example.social_network.dto;

import com.example.social_network.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.social_network.utils.CustomDateFormatter.formatter2;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PostDto {
        private Long id;
        private Profile profile;
        private String content;
        private String createdAt = LocalDateTime.now().format(formatter2);
        private int likesCount = 0;
        private int sharesCount = 0;
        private int commentsCount = 0;
        private List<Like> likes = new ArrayList<>();
        private List<String> postAttachments = new ArrayList<>();
        private List<TrackDto> postTracks;
        private boolean isLiked = false;
        private boolean isPinned = false;
        private Set<String> tags = new HashSet<>();
}
