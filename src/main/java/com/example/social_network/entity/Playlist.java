package com.example.social_network.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlists")
@Getter
@Setter
@ToString
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDateTime creationDate;
    @Nullable
    private String icon_url;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Profile owner;
}
