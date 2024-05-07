package com.example.social_network.controllers;

import com.example.social_network.entity.Post;
import com.example.social_network.repositories.*;
import com.example.social_network.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:3000")
public class StatisticsController {

    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final PageVisitService pageVisitService;
    private final FollowersService followersService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PageVisitRepository pageVisitRepository;

    @Autowired
    public StatisticsController(PostService postService, LikeService likeService, CommentService commentService, PageVisitService pageVisitService, FollowersService followersService, UserRepository userRepository, CommentRepository commentRepository, PostRepository postRepository, LikeRepository likeRepository, PageVisitRepository pageVisitRepository) {
        this.postService = postService;
        this.likeService = likeService;
        this.commentService = commentService;
        this.pageVisitService = pageVisitService;
        this.followersService = followersService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.pageVisitRepository = pageVisitRepository;
    }



    @PostMapping("/postsPerPeriod")
    public ResponseEntity<Integer> getPostsPerPeriod(@RequestPart String profileId, @RequestPart String fromDate, @RequestPart String toDate) {
        System.out.println("\n\n\n\nProfileid = " + profileId);
        System.out.println("\n\n\n\nfromDate = " + fromDate);
        System.out.println("\n\n\n\ntoDate = " + toDate);
        Long profileIdLong = Long.parseLong(profileId);
        return new ResponseEntity<>(postService.getPostsByProfileAndDateRange(profileIdLong, fromDate, toDate), HttpStatus.OK);
    }

    @PostMapping("/followersPerPeriod")
    public ResponseEntity<Integer> getFollowersPerPeriod(@RequestPart String userId, @RequestPart String fromDate, @RequestPart String toDate) {

        Long userIdLong = Long.parseLong(userId);
        return new ResponseEntity<>(followersService.getFollowersByProfileAndDateRange(userIdLong, fromDate, toDate), HttpStatus.OK);
    }

    @PostMapping("/followingPerPeriod")
    public ResponseEntity<Integer> getFollowingPerPeriod(@RequestPart String userId, @RequestPart String fromDate, @RequestPart String toDate) {

        Long userIdLong = Long.parseLong(userId);
        return new ResponseEntity<>(followersService.getFollowingByProfileAndDateRange(userIdLong, fromDate, toDate), HttpStatus.OK);
    }

    @PostMapping("/likesPerPeriod")
    public ResponseEntity<Long> getLikesPerPeriod(@RequestPart String profileId, @RequestPart String fromDate, @RequestPart String toDate) {
        System.out.println("\n\n\n\nProfileid = " + profileId);
        System.out.println("\n\n\n\nfromDate = " + fromDate);
        System.out.println("\n\n\n\ntoDate = " + toDate);
        Long profileIdLong = Long.parseLong(profileId);

        List<Post> posts = postRepository.findByProfile_ProfileId(profileIdLong);
        AtomicReference<Long> likesPerPeriodSum = new AtomicReference<>(0L);
        posts.forEach(post ->
        {
            int likes = likeService.getLikesByPostAndDateRange(post.getId(), fromDate, toDate);
            likesPerPeriodSum.updateAndGet(v -> v + likes);
        });

        return new ResponseEntity<>(likesPerPeriodSum.get(), HttpStatus.OK);
    }

    @PostMapping("/commentsPerPeriod")
    public ResponseEntity<Long> getCommentsPerPeriod(@RequestPart String profileId, @RequestPart String fromDate, @RequestPart String toDate) {
        System.out.println("\n\n\n\nProfileid = " + profileId);
        System.out.println("\n\n\n\nfromDate = " + fromDate);
        System.out.println("\n\n\n\ntoDate = " + toDate);
        Long profileIdLong = Long.parseLong(profileId);

        List<Post> posts = postRepository.findByProfile_ProfileId(profileIdLong);
        AtomicReference<Long> commentsPerPeriodSum = new AtomicReference<>(0L);
        posts.forEach(post ->
        {
            int comments = commentService.getCommentsByPostAndDateRange(post.getId(), fromDate, toDate);
            commentsPerPeriodSum.updateAndGet(v -> v + comments);
        });

        return new ResponseEntity<>(commentsPerPeriodSum.get(), HttpStatus.OK);
    }


    @PostMapping("/visitsPerPeriod")
    public ResponseEntity<Integer> getVisitsPerPeriod(@RequestPart String userId, @RequestPart String fromDate, @RequestPart String toDate) {

        Long userIdLong = Long.parseLong(userId);
        return new ResponseEntity<>(pageVisitService.getVisitByProfileAndDateRange(userIdLong, fromDate, toDate), HttpStatus.OK);
    }

}