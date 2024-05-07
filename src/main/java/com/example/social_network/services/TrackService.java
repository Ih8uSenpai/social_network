package com.example.social_network.services;

import com.example.social_network.entity.Track;
import com.example.social_network.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.example.social_network.utils.Constants.uploadPath;

@Service
public class TrackService {

    private final TrackRepository trackRepository;


    @Autowired
    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public Track saveTrack(Track track, MultipartFile file, MultipartFile icon) {
        try {
            if (!file.isEmpty()) {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + file.getOriginalFilename());
                Files.write(path, bytes);


                String correctPath = path.toString().replace("\\", "/");
                track.setUrl(correctPath);
            }
            if (icon != null && !icon.isEmpty()) {
                byte[] bytes = icon.getBytes();
                Path path = Paths.get(uploadPath + icon.getOriginalFilename());
                Files.write(path, bytes);


                String correctPath = path.toString().replace("\\", "/");
                track.setIcon_url(correctPath);
            }
            return trackRepository.save(track);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }
}

