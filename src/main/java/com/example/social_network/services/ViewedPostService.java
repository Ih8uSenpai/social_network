package com.example.social_network.services;

import com.example.social_network.entity.ViewedPost;
import com.example.social_network.repositories.ViewedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViewedPostService {

    @Autowired
    private ViewedPostRepository viewedPostRepository;

    public void markPostAsViewed(Long userId, Long postId) {
        if (!viewedPostRepository.existsByUserIdAndPostId(userId, postId)) {
            ViewedPost viewedPost = new ViewedPost(userId, postId);
            viewedPostRepository.save(viewedPost);
        }
    }
}
