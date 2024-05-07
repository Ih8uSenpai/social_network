package com.example.social_network.repositories;

import com.example.social_network.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByProfile_ProfileId(Long profileId);

    @Query("SELECT p FROM Post p WHERE p.profile.id = :profileId AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Post> findPostsByProfileIdAndDateRange(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT p FROM Post p WHERE p.profile.id IN :profileIds AND p.id NOT IN " +
            "(SELECT vp.postId FROM ViewedPost vp WHERE vp.userId = :userId) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findTop100UnviewedPostsByFollowedProfiles(@Param("profileIds") List<Long> profileIds, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.id IN (select l.postId from Like l where l.userId = :userId)" +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsLikedByUser(@Param("userId") Long userId);
}
