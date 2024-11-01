package com.example.social_network.utils;

import com.example.social_network.entity.Post;
import com.example.social_network.entity.PostTrack;
import com.example.social_network.entity.Track;

public class Mappers {
    public static PostTrack convertTrackToPostTrack(Track track, Post post) {
        PostTrack postTrack = new PostTrack();
        postTrack.setTitle(track.getTitle());
        postTrack.setArtist(track.getArtist());
        postTrack.setUrl(track.getUrl());
        postTrack.setIcon_url(track.getIcon_url());
        postTrack.setPost(post);
        return postTrack;
    }
    public static Track convertPostTrackToTrack(PostTrack postTrack) {
        Track track = new Track();
        track.setTitle(postTrack.getTitle());
        track.setArtist(postTrack.getArtist());
        track.setUrl(postTrack.getUrl());
        track.setIcon_url(postTrack.getIcon_url());
        track.setId(postTrack.getId());
        return track;
    }
}
