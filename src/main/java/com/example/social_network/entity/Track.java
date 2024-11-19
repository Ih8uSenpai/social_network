package com.example.social_network.entity;

import com.example.social_network.dto.TrackDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tracks")
@ToString
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    @Nullable
    private String url;
    @Nullable
    private String icon_url;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTrack> postTracks = new ArrayList<>();

    public TrackDto toTrackDto() {
        return new TrackDto(id, title, artist, url, icon_url);
    }
}
