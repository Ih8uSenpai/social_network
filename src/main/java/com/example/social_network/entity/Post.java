package com.example.social_network.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "posts")
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "profileId")
    private Profile profile;


    @Column(columnDefinition = "TEXT")
    @Nullable
    private String content;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private int likesCount = 0;

    @Column(nullable = false)
    private int sharesCount = 0;

    @Column(nullable = false)
    private int commentsCount = 0;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();
    public boolean isLikedByUser(Long userId) {
        return likes.stream().anyMatch(like -> like.getUserId().equals(userId));
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Nullable
    private List<PostAttachment> postAttachments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Nullable
    private List<PostTrack> postTracks = new ArrayList<>();

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Nullable
    private List<Comment> comments = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    public void addAttachment(PostAttachment postAttachment) {
        postAttachments.add(postAttachment);
        postAttachment.setPost(this);
    }

    public void removeAttachment(PostAttachment postAttachment) {
        postAttachments.remove(postAttachment);
        postAttachment.setPost(null);
    }
}
