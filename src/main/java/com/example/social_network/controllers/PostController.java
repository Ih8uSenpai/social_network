package com.example.social_network.controllers;

import com.example.social_network.dto.CommentDto;
import com.example.social_network.dto.PostDto;
import com.example.social_network.entity.*;
import com.example.social_network.repositories.CommentRepository;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.services.CommentService;
import com.example.social_network.services.PostService;
import com.example.social_network.services.ViewedPostService;
import com.example.social_network.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.social_network.utils.CustomDateFormatter.formatter2;

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


    @GetMapping("/{postId}/comments")
    @Transactional
    public ResponseEntity<?> getPostComment(@PathVariable Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).stream().filter((value) -> value.getParentComment() == null).sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList();
        List<CommentDto> commentDtos = new ArrayList<>();
        comments.forEach(comment ->
        {
            commentDtos.add(commentService.convertCommentToDto(comment));
        });

        return ResponseEntity.ok(commentDtos);
    }

    @PostMapping("/mark-viewed")
    public ResponseEntity<?> markPostAsViewed(@RequestBody Long postId) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();
        viewedPostService.markPostAsViewed(user.getUserId(), postId);
        return new ResponseEntity<>("Post with id = " + postId + " viewed", HttpStatus.OK);
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

        List<PostDto> postDtos = new ArrayList<>();
        sortedPosts.forEach(post ->
        {
            PostDto postDto = new PostDto(post.getId(), post.getProfile(), post.getContent(), post.getCreatedAt().format(formatter2), post.getLikesCount(), post.getSharesCount(), post.getCommentsCount(), post.getLikes(), post.getPostAttachments().stream()
                    .map(PostAttachment::getUrl)
                    .collect(Collectors.toList()), post.isLikedByUser(userId));
            postDtos.add(postDto);
        });
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/liked")
    public ResponseEntity<List<PostDto>> getLikedPosts() {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();

        List<Post> posts = postService.getPostsLikedByUser(user.getUserId());
        List<Post> sortedPosts = posts.stream()
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt())).toList();

        List<PostDto> postDtos = new ArrayList<>();
        sortedPosts.forEach(post ->
        {
            PostDto postDto = new PostDto(post.getId(), post.getProfile(), post.getContent(), post.getCreatedAt().format(formatter2), post.getLikesCount(), post.getSharesCount(), post.getCommentsCount(), post.getLikes(), post.getPostAttachments().stream()
                    .map(PostAttachment::getUrl)
                    .collect(Collectors.toList()), post.isLikedByUser(user.getUserId()));
            postDtos.add(postDto);
        });
        return ResponseEntity.ok(postDtos);
    }
}
