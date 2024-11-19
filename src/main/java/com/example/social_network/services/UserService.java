package com.example.social_network.services;

import com.example.social_network.dto.UserRegistrationDto;
import com.example.social_network.entity.Profile;
import com.example.social_network.entity.User;
import com.example.social_network.repositories.ProfileRepository;
import com.example.social_network.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static com.example.social_network.utils.CustomDateFormatter.formatter1;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    public User registerUser(UserRegistrationDto registrationDto) {
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPasswordHash(passwordEncoder.encode(registrationDto.getPasswordHash()));
        newUser.setEmail(registrationDto.getEmail());


        newUser.setCreatedAt(LocalDateTime.now().format(formatter1));

        // Сохранение пользователя
        User savedUser = userRepository.save(newUser);

        // Создание и сохранение профиля
        Profile profile = new Profile();
        profile.setUser(savedUser);
        profile.setFirstName(registrationDto.getFirstName());
        profile.setLastName(registrationDto.getLastName());
        profile.setTag(registrationDto.getTag());
        profile.setFollowersCount(0L);
        profile.setFollowingCount(0L);
        // Остальные поля профиля
        profileRepository.save(profile);




        return savedUser;
    }


    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("The password you entered was incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void changeUsername(Long userId, String newUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("Username already taken");
        }

        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Transactional
    public void changeEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getIsActive()) {
            throw new IllegalStateException("User is already deactivated");
        }

        user.setIsActive(false);
        user.setDeactivationDate(LocalDateTime.now());

        // Добавляем токен в чёрный список
        jwtBlacklistService.addToBlacklist(token);

        userRepository.save(user);
    }

    @Transactional
    public void restoreUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getIsActive()) {
            throw new IllegalStateException("User is already active");
        }

        if (user.getDeactivationDate() != null && user.getDeactivationDate().plusDays(30).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("The restoration period has expired");
        }

        user.setIsActive(true);
        user.setDeactivationDate(null); // Очистить дату деактивации
        userRepository.save(user);
    }



}
