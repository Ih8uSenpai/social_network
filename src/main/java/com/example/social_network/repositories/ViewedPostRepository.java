package com.example.social_network.repositories;

import com.example.social_network.entity.ViewedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewedPostRepository extends JpaRepository<ViewedPost, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
