package com.example.social_network.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table
public class UserToUserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Column(name = "interest_score", nullable = false)
    private double interestScore;

    public UserToUserInterest(Long userId, Long targetUserId, double interestScore) {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.interestScore = interestScore;
    }
}