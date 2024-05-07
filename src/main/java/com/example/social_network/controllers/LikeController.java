package com.example.social_network.controllers;

import com.example.social_network.entity.Like;
import com.example.social_network.entity.User;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.services.LikeService;
import com.example.social_network.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class LikeController {

    private final LikeService likeService;
    private final UserRepository userRepository;

    @Autowired
    public LikeController(LikeService likeService, UserRepository userRepository) {
        this.likeService = likeService;
        this.userRepository = userRepository;
    }

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<?> addLike(@PathVariable Long postId, @RequestBody Map<String, Long> userIdMap) {
        Long userId = userIdMap.get("userId");
        Like like = likeService.addLikeToPost(postId, userId);
        return new ResponseEntity<>(like, HttpStatus.CREATED);
    }
    @DeleteMapping("/posts/{postId}/likes")
    @Transactional
    public ResponseEntity<?> removeLike(@PathVariable Long postId,
                                        @RequestParam(required = false) Long commentId) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();
        likeService.removeLike(user.getUserId(), postId, commentId);
        return ResponseEntity.ok().build();
    }
}
