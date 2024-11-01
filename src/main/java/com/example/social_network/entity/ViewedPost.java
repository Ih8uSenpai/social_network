package com.example.social_network.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "viewed_posts")
@Getter
@Setter
@RequiredArgsConstructor
public class ViewedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "view_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ViewType viewType;

    public ViewedPost(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public enum ViewType {
        QUICK,
        MEDIUM,
        LONG
    }
}
