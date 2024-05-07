package com.example.social_network.repositories;

import com.example.social_network.entity.PlaylistTracks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistTracksRepository extends JpaRepository<PlaylistTracks, Long> {
    List<PlaylistTracks> findAllByPlaylistId(Long playlistId);
    Optional<PlaylistTracks> findByPlaylistIdAndTrackId(Long playlistId, Long TrackId);
}
