package com.example.social_network.repositories;

import com.example.social_network.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndPostIdAndCommentId(Long userId, Long postId, Long commentId);
    List<Like> findByPostId(Long postId);

    @Query("SELECT l FROM Like l WHERE l.postId = :postId AND l.createdAt BETWEEN :startDate AND :endDate")
    List<Like> findLikesByPostIdAndDateRange(
            @Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    void deleteByUserIdAndPostIdAndCommentId(Long userId, Long postId, Long commentId);
}
