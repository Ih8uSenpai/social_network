package com.example.social_network.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "profileId")
    private Profile profile;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now(); // Устанавливаем текущее время при создании поста

    @Column(nullable = false)
    private int likesCount = 0; // Счетчик лайков

    @Column(nullable = false)
    private int sharesCount = 0; // Счетчик ретвитов

    @Column(nullable = false)
    private int commentsCount = 0; // Счетчик комментариев

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();
    public boolean isLikedByUser(Long userId) {
        return likes.stream().anyMatch(like -> like.getUserId().equals(userId));
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Nullable
    private List<PostAttachment> postAttachments = new ArrayList<>();


    public void addAttachment(PostAttachment postAttachment) {
        postAttachments.add(postAttachment);
        postAttachment.setPost(this);
    }

    public void removeAttachment(PostAttachment postAttachment) {
        postAttachments.remove(postAttachment);
        postAttachment.setPost(null);
    }
}
