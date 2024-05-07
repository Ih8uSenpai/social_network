package com.example.social_network.repositories;

import com.example.social_network.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Optional<Playlist> findByNameAndDescription(String name, String description);
}