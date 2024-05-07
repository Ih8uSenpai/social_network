package com.example.social_network.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "playlist_tracks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"playlist_id", "track_id"})
})
public class PlaylistTracks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "playlist_id", nullable = false)
    @ManyToOne
    private Playlist playlist;

    @JoinColumn(name = "track_id", nullable = false)
    @ManyToOne
    private Track track;
}
