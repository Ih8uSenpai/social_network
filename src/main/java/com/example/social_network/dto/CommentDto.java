package com.example.social_network.dto;

import com.example.social_network.entity.Comment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class CommentDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String url;
    private List<CommentDto> replies = new ArrayList<>();
    private String parentTag;
}
