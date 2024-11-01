package com.example.social_network.services;

import com.example.social_network.dto.PostData;
import com.example.social_network.entity.Post;
import com.example.social_network.repositories.FollowerRepository;
import com.example.social_network.repositories.PostRepository;
import com.example.social_network.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.social_network.utils.CustomDateFormatter.formatter2;

@Service
public class PostService {


    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final FollowerRepository followerRepository;

    @Autowired
    public PostService(PostRepository postRepository, ProfileRepository profileRepository, FollowerRepository followerRepository) {
        this.postRepository = postRepository;
        this.profileRepository = profileRepository;
        this.followerRepository = followerRepository;
    }



    public PostData getPostData(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostData postData = convertToPostData(post);
        postData.setUserLiked(post.isLikedByUser(userId));

        return postData;
    }

    private PostData convertToPostData(Post post) {
        PostData postData = new PostData();

        postData.setId(post.getId());
        postData.setContent(post.getContent());
        postData.setCreatedAt(post.getCreatedAt().format(formatter2));
        postData.setLikesCount(post.getLikesCount());
        postData.setSharesCount(post.getSharesCount());
        postData.setCommentsCount(post.getCommentsCount());
        postData.setProfile(post.getProfile());

        return postData;
    }

    public int getPostsByProfileAndDateRange(Long profileId, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(toDate, formatter);

        return postRepository.findPostsByProfileIdAndDateRange(profileId, startDateTime, endDateTime).size();
    }

    public List<Post> getNewsFeed(Long userId) {
        // Получаем ID пользователей, на которых подписан текущий пользователь
        List<Long> followedUserIds = followerRepository.findByFollower_UserId(userId).stream()
                .map(follower -> follower.getUser().getUserId()).toList();

        List<Long> followedProfileIds = new ArrayList<>();
        followedUserIds.forEach(id ->
        {
            followedProfileIds.add(profileRepository.findByUser_UserId(id).get().getProfileId());
        });

        // Получаем до 100 непросмотренных постов
        return postRepository.findTop100UnviewedPostsByFollowedProfiles(followedProfileIds, userId, PageRequest.of(0, 100));
    }

    public List<Post> getPostsLikedByUser(Long userId) {
        // Получаем ID пользователей, на которых подписан текущий пользователь
        return postRepository.findPostsLikedByUser(userId);
    }

    public List<Post> findPostsByTagSubstring(String substring) {
        return postRepository.findByTagContaining(substring);
    }

}
