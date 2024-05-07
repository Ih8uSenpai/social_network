package com.example.social_network.services;

import com.example.social_network.dto.UserRegistrationDto;
import com.example.social_network.entity.Profile;
import com.example.social_network.entity.User;
import com.example.social_network.repositories.ProfileRepository;
import com.example.social_network.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
