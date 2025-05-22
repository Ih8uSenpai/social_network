package com.example.social_network.services;

import com.example.social_network.entity.Like;
import com.example.social_network.entity.Post;
import com.example.social_network.entity.User;
import com.example.social_network.repositories.LikeRepository;
import com.example.social_network.repositories.PostRepository;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserInterestsService userInterestsService;
    private final UserToUserInterestService userToUserInterestService;

    @Transactional
    public Like addLikeToPost(Long postId, Long userId) {
        // Проверяем, существует ли пост
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // добавляем интерес текущего пользователя к владельцу поста
        User post_creator = post.getProfile().getUser();
        userToUserInterestService.saveOrUpdateInterest(userId, post_creator.getUserId(), 5);

        // Создаем новый лайк
        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        like.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Сохраняем лайк
        like = likeRepository.save(like);

        // Обновляем счетчик лайков в посте
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);

        userInterestsService.addLike(userId, post);

        return like;
    }

    @Transactional
    public void removeLike(Long userId, Long postId, Long commentId) {
        likeRepository.deleteByUserIdAndPostIdAndCommentId(userId, postId, commentId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikesCount(post.getLikesCount() - 1);
        postRepository.save(post);

        userInterestsService.removeLike(userId, post);

        // убираем интерес текущего пользователя к владельцу поста
        User post_creator = post.getProfile().getUser();
        userToUserInterestService.saveOrUpdateInterest(userId, post_creator.getUserId(), -5);
    }

    public int getLikesByPostAndDateRange(Long postId, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(toDate, formatter);

        return likeRepository.findLikesByPostIdAndDateRange(postId, startDateTime, endDateTime).size();
    }
}
