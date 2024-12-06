package com.example.social_network.services;

import com.example.social_network.dto.TrackDto;
import com.example.social_network.entity.Track;
import com.example.social_network.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final StaticFileService staticFileService;

    @Autowired
    public TrackService(TrackRepository trackRepository, StaticFileService staticFileService) {
        this.trackRepository = trackRepository;
        this.staticFileService = staticFileService;
    }

    public Track saveTrack(Track track, MultipartFile file, MultipartFile icon) {
        if (!file.isEmpty()) {

            staticFileService.uploadFile(file);
            track.setUrl("uploads/" + file.getOriginalFilename());
        }
        if (icon != null && !icon.isEmpty()) {
            staticFileService.uploadFile(icon);
            track.setIcon_url("uploads/" + icon.getOriginalFilename());
        }
        return trackRepository.save(track);
    }


    public List<TrackDto> getAllTracks() {
        List<Track> tracks = trackRepository.findAll();
        return tracks.stream().map(Track::toTrackDto).toList();
    }

    public List<Track> searchTracks(String searchString) {
        return trackRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(searchString, searchString);
    }
}

