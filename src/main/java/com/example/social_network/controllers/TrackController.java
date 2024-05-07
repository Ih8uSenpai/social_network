package com.example.social_network.controllers;

import com.example.social_network.entity.Track;
import com.example.social_network.services.TrackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TrackController(TrackService trackService, ObjectMapper objectMapper) {
        this.trackService = trackService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadTrack(@RequestPart("track") String trackStr,
                                         @RequestPart("file") MultipartFile file,
                                         @Nullable @RequestPart MultipartFile icon) {
        try {
            Track track = objectMapper.readValue(trackStr, Track.class);
            Track savedTrack = trackService.saveTrack(track, file, icon);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTrack);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload track");
        }
    }

    @GetMapping
    public ResponseEntity<List<Track>> getAllTracks() {
        List<Track> tracks = trackService.getAllTracks();
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }
}

