package com.example.social_network.services;

import com.example.social_network.entity.User;
import com.example.social_network.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCleanupService {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Выполняется ежедневно в полночь
    @Transactional
    public void deleteExpiredUsers() {
        List<User> usersToDelete = userRepository.findAll().stream()
                .filter(user -> !user.getIsActive() &&
                        user.getDeactivationDate() != null &&
                        user.getDeactivationDate().plusDays(30).isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        userRepository.deleteAll(usersToDelete);
    }
}
