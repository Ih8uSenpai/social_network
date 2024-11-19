package com.example.social_network.dto;

import com.example.social_network.entity.Track;
import jakarta.annotation.Nullable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TrackDto {
    private Long id;
    private String title;
    private String artist;
    private String url;
    private String icon_url;
}
