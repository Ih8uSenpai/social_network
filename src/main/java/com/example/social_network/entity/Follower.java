package com.example.social_network.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "followers")
@Getter
@Setter
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followingId")
    private User user; // Ссылка на пользователя, который имеет этого фолловера

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followerId")
    private User follower; // ID фолловера

    public Follower() {
    }

    public Follower(User user, User follower) {
        this.user = user;
        this.follower = follower;
    }

    @Column(name = "followed_at")
    private LocalDateTime followedAt;
}
