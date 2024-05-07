package com.example.social_network.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private String firstName;
    private String lastName;
    private String tag;

    private String about_me;
    private String country;
    private String interests;

    private LocalDate birthdate;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "profile_banner_picture_url")
    private String profileBannerPictureUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "followers_count")
    private Long followersCount;
    @Column(name = "following_count")
    private Long followingCount;


}