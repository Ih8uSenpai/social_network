package com.example.social_network.repositories;

import com.example.social_network.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
    boolean existsByUser_UserIdAndFollower_UserId(Long userId, Long followerId);
    Follower findByUser_UserIdAndFollower_UserId(Long userId, Long followerId);

    List<Follower> findByFollower_UserId(Long followerId);
    List<Follower> findByUser_UserId(Long userId);

    @Query("SELECT f FROM Follower f WHERE f.follower.userId = :userId AND f.followedAt BETWEEN :startDate AND :endDate")
    List<Follower> findFollowingByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT f FROM Follower f WHERE f.user.userId = :userId AND f.followedAt BETWEEN :startDate AND :endDate")
    List<Follower> findFollowersByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}