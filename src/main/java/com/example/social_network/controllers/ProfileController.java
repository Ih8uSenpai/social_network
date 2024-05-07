package com.example.social_network.controllers;

import com.example.social_network.dto.PostDto;
import com.example.social_network.entity.*;
import com.example.social_network.repositories.CommentRepository;
import com.example.social_network.repositories.PostRepository;
import com.example.social_network.repositories.User2PhotosRepository;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.services.PostAttachmentService;
import com.example.social_network.services.ProfileService;
import com.example.social_network.services.PageVisitService;
import com.example.social_network.utils.SecurityUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.social_network.utils.Constants.uploadPath;
import static com.example.social_network.utils.CustomDateFormatter.formatter2;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final PageVisitService pageVisitService;
    private final PostAttachmentService attachmentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final User2PhotosRepository user2PhotosRepository;

    @GetMapping("/other/{userId}")
    public ResponseEntity<Profile> getUserProfile(@PathVariable Long userId) {
        Optional<Profile> profile = profileService.getProfileByUserId(userId);

        return profile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<Profile> getCurrentUserProfile() {
        System.out.println("get mapping starts");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Profile> profile = profileService.getProfileByUsername(username);
        System.out.println("profile = " + profile);
        return profile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody Profile updatedProfile, Principal principal) {
        String username = principal.getName();
        Optional<Profile> profile = profileService.updateProfile(username, updatedProfile);
        return profile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/upload-banner/{userId}")
    public ResponseEntity<?> uploadProfileBanner(@PathVariable Long userId, @RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = profileService.uploadProfileImage(file, userId, "banner");
            return ResponseEntity.ok().body(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке изображения: " + e.getMessage());
        }
    }
    @PostMapping("/upload-icon/{userId}")
    public ResponseEntity<?> uploadProfileIcon(@PathVariable Long userId, @RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = profileService.uploadProfileImage(file, userId, "icon");
            return ResponseEntity.ok().body(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке изображения: " + e.getMessage());
        }
    }

    @GetMapping("/{profileId}/posts")
    public ResponseEntity<List<PostDto>> getPostsByProfile(@PathVariable Long profileId) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();
        Optional<Profile> profile = profileService.getProfileById(profileId);

        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Post> posts = profileService.getPostsByProfileId(profileId);
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

    @GetMapping("/{profileId}/photos")
    public ResponseEntity<List<String>> getPhotos(@PathVariable Long profileId) {
        Profile profile = profileService.getProfileById(profileId).get();
        List<User2Photos> user2Photos = user2PhotosRepository.findAllByUser(profile.getUser());

        List<String> photos = new ArrayList<>();
        user2Photos.forEach(user2Photos1 -> photos.add(user2Photos1.getUrl()));
        return ResponseEntity.ok(photos);
    }

    @PostMapping("/{profileId}/posts")
    public ResponseEntity<Post> createPost(@PathVariable Long profileId, @RequestPart String content, @Nullable @RequestPart("files") List<MultipartFile> files) {
        Optional<Profile> profile = profileService.getProfileById(profileId);

        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Post post = new Post();
        post.setContent(content);
        post.setProfile(profile.get());

        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();

        // также нужно к посту прикрепить вложения setAttachments перед передачей в метод
        if (files != null) {
            List<PostAttachment> postAttachments = new ArrayList<>();
            files.forEach(multipartFile ->
            {
                PostAttachment postAttachment = new PostAttachment();
                postAttachment.setType(multipartFile.getContentType());
                postAttachment.setPost(post);

                try {
                    byte[] bytes = multipartFile.getBytes();
                    Path path = Paths.get(uploadPath + multipartFile.getOriginalFilename());
                    Files.write(path, bytes);
                    String correctPath = path.toString().replace("\\", "/");
                    postAttachment.setUrl(correctPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                postAttachments.add(postAttachment);
                User2Photos user2Photos = new User2Photos();
                user2Photos.setUser(user);
                user2Photos.setUrl(postAttachment.getUrl());
                user2PhotosRepository.save(user2Photos);
            });

            post.setPostAttachments(postAttachments);
        }

        Post savedPost = profileService.savePost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @PostMapping("/upload")
    public ResponseEntity<PostAttachment> uploadAttachment(@RequestPart("file") MultipartFile file,
                                                           @RequestPart("postId") Long postId) {
        PostAttachment attachment = new PostAttachment();

        attachment.setPost(postRepository.findById(postId).orElse(null));
        PostAttachment savedAttachment = attachment.getPost() != null ? attachmentService.saveAttachment(attachment, file): null;
        return new ResponseEntity<>(savedAttachment, HttpStatus.CREATED);
    }

    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<?> addCommentToThePost(@PathVariable Long postId, @RequestPart String content, @Nullable @RequestPart MultipartFile file) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElse(null);
        if (user == null)
            return ResponseEntity.ok("User doesn't exist");
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setUserId(user.getUserId());

        Post post = postRepository.findById(postId).get();
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.OK);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostAttachment>> getAttachmentsByPostId(@PathVariable Long postId) {
        List<PostAttachment> attachments = attachmentService.getAttachmentsByPostId(postId);
        return new ResponseEntity<>(attachments, HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<Profile> searchProfiles(@RequestParam String query) {
        List<Profile> profiles = profileService.searchProfilesByTag(query);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Profile> current_profile = profileService.getProfileByUsername(username);
        current_profile.ifPresent(profiles::remove);
        return profiles;
    }

    @PostMapping("/follow/{userId}")
    public ResponseEntity<?> followUser(@PathVariable Long userId, Principal principal) {
        String currentUsername = principal.getName();
        boolean result = profileService.followUser(currentUsername, userId);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Не удалось выполнить подписку");
        }
    }

    @GetMapping("/isFollowing/{userId}")
    public ResponseEntity<?> isFollowing(@PathVariable Long userId, Principal principal) {
        String currentUsername = principal.getName();
        boolean isFollowing = profileService.isFollowing(currentUsername, userId);
        return ResponseEntity.ok(Collections.singletonMap("isFollowing", isFollowing));
    }

    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long userId, Principal principal) {
        String currentUsername = principal.getName();
        boolean result = profileService.unfollowUser(currentUsername, userId);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Ошибка при попытке отписаться");
        }
    }

    // В вашем ProfileController
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<Profile>> getFollowers(@PathVariable Long userId) {
        List<Profile> followers = profileService.getFollowers(userId);

        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<Profile>> getFollowing(@PathVariable Long userId) {
        List<Profile> following = profileService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @PostMapping("/visit/{userId}")
    public ResponseEntity<?> registerVisit(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.findByUsername(authentication.getName()).ifPresent(user -> pageVisitService.recordVisit(userId, user.getUserId()));
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

}