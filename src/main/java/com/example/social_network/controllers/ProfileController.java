package com.example.social_network.controllers;

import com.example.social_network.dto.PostDto;
import com.example.social_network.entity.*;
import com.example.social_network.repositories.*;
import com.example.social_network.services.*;
import com.example.social_network.utils.SecurityUtils;
import com.example.social_network.utils.TagExtractor;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.example.social_network.controllers.PostController.convertPostsToDTO;
import static com.example.social_network.utils.Constants.uploadPath;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "${FRONTEND_URL}")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;
    private final PageVisitService pageVisitService;
    private final PostAttachmentService attachmentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final User2PhotosRepository user2PhotosRepository;

    private final TagExtractor tagExtractor;
    private final UserInterestsService userInterestsService;

    private final UserToUserInterestService userToUserInterestService;

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
        Long pinnedPostId = profile.get().getPinnedPostId();

        List<Post> sortedPosts = posts.stream()
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .collect(Collectors.toList());

        if (pinnedPostId != null) {
            Optional<Post> pinnedPost = sortedPosts.stream()
                    .filter(post -> post.getId().equals(pinnedPostId))
                    .findFirst();

            if (pinnedPost.isPresent()) {
                sortedPosts.remove(pinnedPost.get()); // Удаляем его из списка
                sortedPosts.add(0, pinnedPost.get()); // Добавляем его в начало списка
            }
        }

        if (pinnedPostId == null) {
            return ResponseEntity.ok(convertPostsToDTO(user, sortedPosts));
        } else {
            List<PostDto> postDtos = convertPostsToDTO(user, sortedPosts);
            postDtos.get(0).setPinned(true);
            return ResponseEntity.ok(postDtos);
        }
    }

    @GetMapping("/{profileId}/photos")
    public ResponseEntity<List<String>> getPhotos(@PathVariable Long profileId) {
        Profile profile = profileService.getProfileById(profileId).get();
        List<User2Photos> user2Photos = user2PhotosRepository.findAllByUser(profile.getUser());

        List<String> photos = new ArrayList<>();
        user2Photos.forEach(user2Photos1 -> photos.add(user2Photos1.getUrl()));
        return ResponseEntity.ok(photos);
    }

    @DeleteMapping("/photo/{index}")
    public ResponseEntity<?> deletePhoto(@PathVariable int index){
        String username = SecurityUtils.getCurrentUsername();
        User currentUser = userRepository.findByUsername(username).get();
        List<User2Photos> user2Photos = user2PhotosRepository.findAllByUser(currentUser);

        Long id = user2Photos.get(index).getId();
        user2PhotosRepository.deleteById(id);
        return ResponseEntity.ok("photo was deleted");
    }

    @DeleteMapping("/{postId}")
    @Transactional
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @RequestBody Long author_id, Principal principal) {
        String currentUsername = principal.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUsername);
        AtomicReference<String> result = new AtomicReference<>();
        currentUser.ifPresent(user -> {
            if (Objects.equals(user.getUserId(), author_id)) {
                postRepository.deleteById(postId);
                result.set("post with id = " + postId + " was deleted");
            } else
                result.set("access denied");
        });

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{profileId}/posts")
    public ResponseEntity<Post> createPost(@PathVariable Long profileId, @RequestPart @Nullable String content,
                                           @Nullable @RequestPart("files") List<MultipartFile> files,
                                           @RequestPart @Nullable List<Track> selectedTracks) throws Exception {
        Optional<Profile> profile = profileService.getProfileById(profileId);

        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Post post = new Post();
        if (content != null) {
            post.setContent(content);
            post.getTags().addAll(tagExtractor.extractTags(content));
        }
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
                    String correctPath = multipartFile.getOriginalFilename();
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


        // прикрепляем трэки (если есть)
        if (selectedTracks != null)
        {
            List<PostTrack> postTracks = new ArrayList<>();
            for (Track track : selectedTracks){
                postTracks.add(new PostTrack(null, track, post));
            }
            post.setPostTracks(postTracks);
        }
        Post savedPost = profileService.savePost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @PostMapping("/upload")
    public ResponseEntity<PostAttachment> uploadAttachment(@RequestPart("file") MultipartFile file,
                                                           @RequestPart("postId") Long postId) {
        PostAttachment attachment = new PostAttachment();

        attachment.setPost(postRepository.findById(postId).orElse(null));
        PostAttachment savedAttachment = attachment.getPost() != null ? attachmentService.saveAttachment(attachment, file) : null;
        return new ResponseEntity<>(savedAttachment, HttpStatus.CREATED);
    }

    @PostMapping("/post/{postId}/comment")
    @Transactional
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

        userInterestsService.addComment(user.getUserId(), post);
        userToUserInterestService.addComment(user.getUserId(), post.getProfile().getUser().getUserId(), post);
        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.OK);
    }

    @PostMapping("/post/{postId}/{commentId}/commentReply")
    public ResponseEntity<?> addReplyToTheComment(@PathVariable Long commentId, @PathVariable Long postId, @RequestPart String content, @Nullable @RequestPart MultipartFile file) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElse(null);
        Comment parentComment = commentRepository.findById(commentId).orElseThrow();
        if (user == null)
            return ResponseEntity.ok("User doesn't exist");
        Comment comment = new Comment();
        comment.setParentComment(parentComment);
        comment.setContent(content);
        comment.setPostId(postId);
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
    @Transactional
    public ResponseEntity<?> followUser(@PathVariable Long userId, Principal principal) {
        String currentUsername = principal.getName();
        User current_user = userRepository.findByUsername(currentUsername).get();

        userToUserInterestService.saveOrUpdateInterest(current_user.getUserId(), userId, 50);
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

    @PostMapping("/pinPost/{postId}")
    public ResponseEntity<?> pinPost(@PathVariable Long postId, Principal principal) {
        String currentUsername = principal.getName();
        userRepository.findByUsername(currentUsername).ifPresent(user ->
        {
            profileService.changePinnedPost(user.getUserId(), postId);
        });
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @PostMapping("/unpinPost")
    public ResponseEntity<?> unpinPost(Principal principal) {
        String currentUsername = principal.getName();
        userRepository.findByUsername(currentUsername).ifPresent(user ->
        {
            profileService.unpinPost(user.getUserId());
        });
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @GetMapping("/findByTag")
    public ResponseEntity<?> findProfileByTag(@RequestParam String tag) {
        Optional<Profile> profile = profileService.findProfileByTag(tag);
        if (profile.isPresent())
            return new ResponseEntity<>(profile.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>("there's no user with tag = " + tag, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{profileId}/change-tag")
    public ResponseEntity<String> changeTag(@PathVariable Long profileId,
                                            @RequestParam String tag) {
        profileService.changeTag(profileId, tag);
        return ResponseEntity.ok("Tag updated successfully");
    }

    @PutMapping("/{profileId}/update-firstname")
    public ResponseEntity<String> changeFirstName(@PathVariable Long profileId,
                                            @RequestParam String firstName) {
        profileService.changeFirstName(profileId, firstName);
        return ResponseEntity.ok("First name updated successfully");
    }

    @PutMapping("/{profileId}/update-lastname")
    public ResponseEntity<String> changeLastName(@PathVariable Long profileId,
                                            @RequestParam String lastName) {
        profileService.changeLastName(profileId, lastName);
        return ResponseEntity.ok("Last name updated successfully");
    }
}