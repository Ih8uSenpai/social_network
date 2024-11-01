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
@Table(name = "user_interests")
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "interest_score", nullable = false)
    private double interestScore;

    public UserInterest(Long userId, String tag, double interestScore) {
        this.userId = userId;
        this.tag = tag;
        this.interestScore = interestScore;
    }

}
