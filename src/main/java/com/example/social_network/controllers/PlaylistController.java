package com.example.social_network.controllers;

import com.example.social_network.dto.TrackDto;
import com.example.social_network.entity.Playlist;
import com.example.social_network.entity.PlaylistTracks;
import com.example.social_network.entity.Track;
import com.example.social_network.entity.User;
import com.example.social_network.repositories.PlaylistRepository;
import com.example.social_network.repositories.PlaylistTracksRepository;
import com.example.social_network.repositories.TrackRepository;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.services.PlaylistService;
import com.example.social_network.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final UserRepository userRepository;
    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTracksRepository playlistTracksRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public PlaylistController(UserRepository userRepository, PlaylistService playlistService, PlaylistRepository playlistRepository, PlaylistTracksRepository playlistTracksRepository, TrackRepository trackRepository) {
        this.userRepository = userRepository;
        this.playlistService = playlistService;
        this.playlistRepository = playlistRepository;
        this.playlistTracksRepository = playlistTracksRepository;
        this.trackRepository = trackRepository;
    }
    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Playlist playlist) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElse(null);
        if (Objects.equals(playlist.getName(), "@me") && Objects.equals(playlist.getDescription(), "@me")) {
            assert user != null;
            playlist.setName(user.getUserId().toString());
        }
        if (playlistRepository.findByNameAndDescription(user.getUserId().toString(), "@me").isPresent())
            return new ResponseEntity<> (playlistRepository.findByNameAndDescription(user.getUserId().toString(), "@me").get(), HttpStatus.OK);
        Playlist newPlaylist = playlistService.createPlaylist(playlist);
        return new ResponseEntity<>(newPlaylist, HttpStatus.CREATED);
    }

    @PostMapping("/{playlistId}/tracks")
    public ResponseEntity<?> addTrackToPlaylist(@PathVariable Long playlistId, @RequestBody Track track) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElse(null);
        if (playlistId == 0) {
            assert user != null;
            playlistId = playlistRepository.findByNameAndDescription(user.getUserId().toString(), "@me").get().getId();
        }
        //Optional<Playlist> updatedPlaylist = playlistService.addTrackToPlaylist(playlistId, track);
        System.out.println("\n\n\n\n\n\nTrack = \n" + track);
        System.out.println("\n\n\n\n\n\nTrack get by id = \n" + playlistRepository.findById(playlistId).get());
        System.out.println("\n\n\n\n\n\nPlaylist get by id = \n" + playlistRepository.findById(playlistId).get());
        PlaylistTracks playlistTracks = new PlaylistTracks();
        playlistTracks.setPlaylist(playlistRepository.findById(playlistId).get());
        playlistTracks.setTrack(trackRepository.findById(track.getId()).get());

        if (playlistTracksRepository.findByPlaylistIdAndTrackId(playlistId, track.getId()).isPresent())
            return new ResponseEntity<>("Track already added", HttpStatus.OK);
        return new ResponseEntity<>(playlistTracksRepository.save(playlistTracks).getTrack(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        return new ResponseEntity<>(playlists, HttpStatus.OK);
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<?> deleteTrackFromPlaylist(@PathVariable Long playlistId, @RequestBody Track track) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElse(null);
        if (playlistId == 0) {
            assert user != null;
            playlistId = playlistRepository.findByNameAndDescription(user.getUserId().toString(), "@me").get().getId();
        }
        PlaylistTracks playlistTracks = playlistTracksRepository.findByPlaylistIdAndTrackId(playlistId, track.getId()).get();
        playlistTracksRepository.delete(playlistTracks);
        return new ResponseEntity<>(playlistTracks, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TrackDto>> getMyPlaylist() {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElse(null);
        Long playlistId = playlistRepository.findByNameAndDescription(user.getUserId().toString(), "@me").get().getId();
        List<PlaylistTracks> playlistTracks = playlistTracksRepository.findAllByPlaylistId(playlistId);
        List<TrackDto> tracks = new ArrayList<>();
        playlistTracks.forEach(playlistTrack -> tracks.add(playlistTrack.getTrack().toTrackDto()));
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TrackDto>> getUserPlaylist(@PathVariable Long userId) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElse(null);
        Long playlistId = playlistRepository.findByNameAndDescription(String.valueOf(userId), "@me").get().getId();
        List<PlaylistTracks> playlistTracks = playlistTracksRepository.findAllByPlaylistId(playlistId);
        List<TrackDto> tracks = new ArrayList<>();
        playlistTracks.forEach(playlistTrack -> tracks.add(playlistTrack.getTrack().toTrackDto()));
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }
}
