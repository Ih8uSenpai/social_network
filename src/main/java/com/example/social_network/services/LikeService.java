package com.example.social_network.services;

import com.example.social_network.entity.Like;
import com.example.social_network.entity.Post;
import com.example.social_network.repositories.LikeRepository;
import com.example.social_network.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    public Like addLikeToPost(Long postId, Long userId) {
        // Проверяем, существует ли пост
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

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

        return like;
    }

    public void removeLike(Long userId, Long postId, Long commentId) {
        likeRepository.deleteByUserIdAndPostIdAndCommentId(userId, postId, commentId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikesCount(post.getLikesCount() - 1);
        postRepository.save(post);
    }

    public int getLikesByPostAndDateRange(Long postId, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(toDate, formatter);

        return likeRepository.findLikesByPostIdAndDateRange(postId, startDateTime, endDateTime).size();
    }
}
