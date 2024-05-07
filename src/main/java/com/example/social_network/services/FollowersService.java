package com.example.social_network.services;

import com.example.social_network.repositories.FollowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FollowersService {
    @Autowired
    private FollowerRepository followersRepository;

    public int getFollowersByProfileAndDateRange(Long userId, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(toDate, formatter);

        return followersRepository.findFollowersByUserIdAndDateRange(userId, startDateTime, endDateTime).size();
    }

    public int getFollowingByProfileAndDateRange(Long userId, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(toDate, formatter);

        return followersRepository.findFollowingByUserIdAndDateRange(userId, startDateTime, endDateTime).size();
    }
}