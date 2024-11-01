package com.example.social_network.repositories;

import com.example.social_network.entity.ViewedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewedPostRepository extends JpaRepository<ViewedPost, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Optional<ViewedPost> findByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT vp FROM ViewedPost vp WHERE vp.userId = :userId")
    List<ViewedPost> findByUserId(@Param("userId") Long userId);

}
