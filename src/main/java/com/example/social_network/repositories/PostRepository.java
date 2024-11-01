package com.example.social_network.repositories;

import com.example.social_network.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Repository
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

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t IN :tags AND p.id NOT IN (SELECT vp.postId FROM ViewedPost vp WHERE vp.userId = :userId) ORDER BY p.createdAt DESC")
    List<Post> findByTagsIn(@Param("tags") Set<String> tags, @Param("userId") Long userId);



    @Query("SELECT p FROM Post p ORDER BY p.likesCount DESC")
    List<Post> findPopularPosts();

    @Query("SELECT p FROM Post p WHERE p.createdAt > :recentDate OR p.likesCount > :minLikes ORDER BY p.createdAt DESC")
    List<Post> findNewOrPopularPosts(@Param("recentDate") LocalDateTime recentDate, @Param("minLikes") int minLikes);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :substring, '%'))")
    List<Post> findByTagContaining(@Param("substring") String substring);


}
