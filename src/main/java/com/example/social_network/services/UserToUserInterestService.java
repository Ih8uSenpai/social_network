package com.example.social_network.services;

import com.example.social_network.entity.Post;
import com.example.social_network.entity.UserToUserInterest;
import com.example.social_network.repositories.UserToUserInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserToUserInterestService {

    private final UserToUserInterestRepository userToUserInterestRepository;

    @Autowired
    public UserToUserInterestService(UserToUserInterestRepository userToUserInterestRepository) {
        this.userToUserInterestRepository = userToUserInterestRepository;
    }

    // Получаем все интересы одного пользователя к другим
    public List<UserToUserInterest> getInterestsByUserId(Long userId) {
        return userToUserInterestRepository.findByUserId(userId);
    }

    // Получаем интерес одного пользователя к конкретному пользователю
    public Optional<UserToUserInterest> getInterestByUserIdAndTargetUserId(Long userId, Long targetUserId) {
        return Optional.ofNullable(userToUserInterestRepository.findByUserIdAndTargetUserId(userId, targetUserId));
    }

    // Создаём или обновляем интерес
    public UserToUserInterest saveOrUpdateInterest(Long userId, Long targetUserId, double interestScore) {
        UserToUserInterest interest = userToUserInterestRepository
                .findByUserIdAndTargetUserId(userId, targetUserId);
        
        if (interest != null) {
            // Обновляем текущий интерес, если он уже существует
            interest.setInterestScore(interestScore);
        } else {
            // Создаем новый интерес
            interest = new UserToUserInterest(userId, targetUserId, interestScore);
        }

        return userToUserInterestRepository.save(interest);
    }

    // Удаление интереса между пользователями
    public void deleteInterest(Long id) {
        userToUserInterestRepository.deleteById(id);
    }

    public void addComment(Long userId,Long targetUserId, Post post) {
        int commentCount = post.getCommentsCount();

        double scoreIncrement = 5.0 / (commentCount + 1);  // Коэффициент уменьшается с каждым новым комментарием

        for (String tag : post.getTags()) {
            saveOrUpdateInterest(userId, targetUserId, scoreIncrement);
        }
    }
}
