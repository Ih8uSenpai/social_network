package com.example.social_network.repositories;

import com.example.social_network.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.createdAt BETWEEN :startDate AND :endDate")
    List<Comment> findCommentByProfileIdAndDateRange(
            @Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}