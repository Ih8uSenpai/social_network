package com.example.social_network.controllers;

import com.example.social_network.dto.CommentDto;
import com.example.social_network.dto.PostDto;
import com.example.social_network.entity.*;
import com.example.social_network.repositories.CommentRepository;
import com.example.social_network.repositories.PostRepository;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.services.CommentService;
import com.example.social_network.services.PostRecommendationService;
import com.example.social_network.services.PostService;
import com.example.social_network.services.ViewedPostService;
import com.example.social_network.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.social_network.utils.CustomDateFormatter.formatter2;
import static com.example.social_network.utils.Mappers.convertPostTrackToTrack;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    private final PostService postService;
    private final ViewedPostService viewedPostService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final PostRepository postRepository;
    private final PostRecommendationService postRecommendationService;


    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getPostComment(@PathVariable Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).stream().filter((value) -> value.getParentComment() == null).sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList();
        List<CommentDto> commentDtos = new ArrayList<>();
        comments.forEach(comment ->
        {
            commentDtos.add(commentService.convertCommentToDto(comment));
        });

        return ResponseEntity.ok(commentDtos);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<PostDto>> getRecommendations(){
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();

        List<Post> posts = postRecommendationService.recommendPosts(user.getUserId());

        return ResponseEntity.ok(convertPostsToDTO(user, posts));
    }

    @PostMapping("/mark-viewed")
    public ResponseEntity<?> markPostAsViewed(@RequestBody ViewedPost viewedPost) {
        postRepository.findById(viewedPost.getPostId()).ifPresentOrElse((post) -> {
            viewedPostService.markPostAsViewed(post, viewedPost);
        }, () -> {throw new NoSuchElementException("Post with id = " + viewedPost.getPostId() + "not found");
        } );

        return new ResponseEntity<>("Post with id = " + viewedPost.getPostId() + " viewed", HttpStatus.OK);
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody Map<String, List<Track>> tracks) {

        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }


    @GetMapping("/feed")
    public ResponseEntity<List<PostDto>> getNewsFeed(@RequestParam Long userId) {
        List<Post> posts = postService.getNewsFeed(userId);
        List<Post> sortedPosts = posts.stream()
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt())).toList();

        User user = userRepository.findById(userId).orElse(null);

        return ResponseEntity.ok(convertPostsToDTO(user, sortedPosts));
    }

    @GetMapping("/liked")
    public ResponseEntity<List<PostDto>> getLikedPosts() {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();

        List<Post> posts = postService.getPostsLikedByUser(user.getUserId());
        List<Post> sortedPosts = posts.stream()
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt())).toList();


        return ResponseEntity.ok(convertPostsToDTO(user, sortedPosts));
    }

    @GetMapping("/search")
    public List<PostDto> searchPosts(@RequestParam String query) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();
        List<Post> posts = postService.findPostsByTagSubstring(query);

        return convertPostsToDTO(user, posts);
    }

    static List<PostDto> convertPostsToDTO(User user, List<Post> sortedPosts) {
        List<PostDto> postDtos = new ArrayList<>();
        sortedPosts.forEach(post ->
        {
            List<Track> tracks = new ArrayList<>();
            post.getPostTracks().forEach(postTrack -> tracks.add(convertPostTrackToTrack(postTrack)));
            PostDto postDto = new PostDto(post.getId(), post.getProfile(), post.getContent(), post.getCreatedAt().format(formatter2), post.getLikesCount(), post.getSharesCount(), post.getCommentsCount(), post.getLikes(), post.getPostAttachments().stream()
                    .map(PostAttachment::getUrl)
                    .collect(Collectors.toList()), tracks, post.isLikedByUser(user.getUserId()),
                    false,post.getTags());
            postDtos.add(postDto);
        });
        return postDtos;
    }

}
