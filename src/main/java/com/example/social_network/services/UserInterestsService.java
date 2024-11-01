package com.example.social_network.services;

import com.example.social_network.entity.Post;
import com.example.social_network.entity.UserInterest;
import com.example.social_network.entity.ViewedPost;
import com.example.social_network.repositories.UserInterestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInterestsService {

    private final UserInterestsRepository userInterestsRepository;

    @Autowired
    public UserInterestsService(UserInterestsRepository userInterestRepository) {
        this.userInterestsRepository = userInterestRepository;
    }

    public List<UserInterest> getUserInterests(Long userId) {
        return userInterestsRepository.findByUserId(userId);
    }

    public void updateUserInterest(Long userId, String tag, double weight) {
        Optional<UserInterest> existingInterest = userInterestsRepository.findByUserIdAndTag(userId, tag);
        if (existingInterest.isPresent()) {
            UserInterest interest = existingInterest.get();
            interest.setInterestScore(interest.getInterestScore() + weight);
            userInterestsRepository.save(interest);
        } else {
            UserInterest newInterest = new UserInterest(userId, tag, weight);
            userInterestsRepository.save(newInterest);
        }
    }

    // Метод для обновления интересов при добавлении лайка
    public void addLike(Long userId, Post post) {
        for (String tag : post.getTags()) {
            updateUserInterest(userId, tag, 1);
        }
    }

    // Метод для обновления интересов при удалении лайка
    public void removeLike(Long userId, Post post) {
        for (String tag : post.getTags()) {
            updateUserInterest(userId, tag, -1);
        }
    }

    // Метод для обновления интересов при просмотре поста
    public void viewPost(Long userId, Post post, ViewedPost.ViewType viewType) {
        switch (viewType){
            case LONG -> {
                for (String tag : post.getTags()) {
                    updateUserInterest(userId, tag, 0.5);
                }
            }
            case MEDIUM -> {
                for (String tag : post.getTags()) {
                    updateUserInterest(userId, tag, 0.25);
                }
            }
            case QUICK -> {
                for (String tag : post.getTags()) {
                    updateUserInterest(userId, tag, 0.1);
                }
            }
        }
    }


    public void removeViewPost(Long userId, Post post, ViewedPost.ViewType viewType) {
        switch (viewType){
            case LONG -> {
                for (String tag : post.getTags()) {
                    updateUserInterest(userId, tag, -0.5);
                }
            }
            case MEDIUM -> {
                for (String tag : post.getTags()) {
                    updateUserInterest(userId, tag, -0.25);
                }
            }
            case QUICK -> {
                for (String tag : post.getTags()) {
                    updateUserInterest(userId, tag, -0.1);
                }
            }
        }
    }

    // Метод для обновления интересов при добавлении комментария
    public void addComment(Long userId, Post post) {
        int commentCount = post.getCommentsCount();

        double scoreIncrement = 1.5 / (commentCount + 1);  // Коэффициент уменьшается с каждым новым комментарием

        for (String tag : post.getTags()) {
            updateUserInterest(userId, tag, scoreIncrement);
        }
    }

    public void removeComment(Long userId, Post post) {
        int commentCount = post.getCommentsCount();

        // Если остались комментарии, уменьшение баллов за последний комментарий
        double scoreDecrement = 1.5 / (commentCount > 0 ? commentCount : 1);  // Коэффициент уменьшается с учетом количества комментариев

        for (String tag : post.getTags()) {
            updateUserInterest(userId, tag, -scoreDecrement);
        }
    }

}
