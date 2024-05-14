package com.example.social_network.services;

import com.example.social_network.entity.Follower;
import com.example.social_network.entity.Post;
import com.example.social_network.entity.Profile;
import com.example.social_network.entity.User;
import com.example.social_network.repositories.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProfileService {

    private ProfileRepository profileRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;
    private PostAttachmentRepository postAttachmentRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository, PostRepository postRepository,
                          FollowerRepository followerRepository, PostAttachmentRepository postAttachmentRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
        this.postAttachmentRepository = postAttachmentRepository;
    }

    public List<Post> getPostsByProfileId(Long profileId) {
        return postRepository.findByProfile_ProfileId(profileId);
    }

    public Post savePost(Post post) {
        Post temp = postRepository.save(post);
        postAttachmentRepository.saveAll(temp.getPostAttachments());
        return temp;
    }

    public Optional<Profile> getProfileByUsername(String username) {
        System.out.println("getProfileByUsername: username = " + username);
        return userRepository.findByUsername(username)
                .flatMap(user -> profileRepository.findByUser_UserId(user.getUserId()));
    }

    public Optional<Profile> getProfileById(Long id) {
        return profileRepository.findById(id);
    }

    public Optional<Profile> getProfileByUserId(Long id) {
        return profileRepository.findByUser_UserId(id);
    }

    // Обновление профиля
    public Optional<Profile> updateProfile(String username, Profile updatedProfile) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    updatedProfile.setUser(user);
                    return Optional.of(profileRepository.save(updatedProfile));
                });
    }

    private final Path rootLocation = Paths.get("uploads");

    public ProfileService() {
        init();
    }

    @PostConstruct
    public void init() {
        try {
            System.out.println("directory creating");
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать каталог для загрузки изображений", e);
        }
    }


    public String uploadProfileImage(MultipartFile file, Long userId, String element) throws IOException {
        log.debug("Загрузка файла: {}, для пользователя с ID: {}", file.getOriginalFilename(), userId);
        if (file.isEmpty()) {
            throw new IOException("Файл пуст");
        }

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destinationFile = this.rootLocation.resolve(Paths.get(filename))
                .normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new IllegalStateException("Нельзя сохранять файлы за пределами текущей директории");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }


        String imageUrl = "http://localhost:8080/uploads/" + filename;

        Optional<Profile> profile = profileRepository.findByUser_UserId(userId);
        switch (element) {
            case "banner" -> profile.ifPresent(value -> {
                value.setProfileBannerPictureUrl(imageUrl);
                profileRepository.save(value);
            });
            case "icon" -> profile.ifPresent(value -> {
                value.setProfilePictureUrl(imageUrl);
                profileRepository.save(value);
            });
            default -> {
            }
        }

        return imageUrl;
    }

    public List<Profile> searchProfilesByTag(String tag) {
        return profileRepository.findByTagContainingIgnoreCase(tag);
    }

    public boolean followUser(String currentUsername, Long userIdToFollow) {
        Optional<User> currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser.isPresent()) {
            if (currentUser.get().getUserId().equals(userIdToFollow)) {
                return false; // Проверка на null и попытку подписаться на самого себя
            }

            if (followerRepository.existsByUser_UserIdAndFollower_UserId(userIdToFollow, currentUser.get().getUserId())) {
                return false; // Пользователь уже подписан
            }
            Follower newFollower = new Follower(userRepository.findById(userIdToFollow).orElse(null), currentUser.get());
            newFollower.setFollowedAt(LocalDateTime.now());
            followerRepository.save(newFollower);

            Optional<User> followingUser = userRepository.findByUsername(currentUsername);
            if (followingUser.isPresent()) {
                Optional<Profile> followingProfile = Optional.of(new Profile());
                followingProfile = profileRepository.findByUser_UserId(followingUser.get().getUserId());
                if (followingProfile.isPresent()){
                    followingProfile.get().setFollowingCount(followingProfile.get().getFollowingCount() + 1);
                    profileRepository.save(followingProfile.get());
                }
            }
            Optional<Profile> profileToBeFollowed = profileRepository.findByUser_UserId(userIdToFollow);
            if (profileToBeFollowed.isPresent()){
                profileToBeFollowed.get().setFollowersCount(profileToBeFollowed.get().getFollowersCount() + 1);
                profileRepository.save(profileToBeFollowed.get());
            }
            return true;
        }
        return false;
    }

    public boolean isFollowing(String username, Long userIdToCheck) {
        Optional<User> currentUser = userRepository.findByUsername(username);
        return currentUser.filter(user -> followerRepository.existsByUser_UserIdAndFollower_UserId(userIdToCheck, user.getUserId())).isPresent();
    }

    public boolean unfollowUser(String currentUsername, Long userIdToUnfollow) {
        Optional<User> currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser.isEmpty() || currentUser.get().getUserId().equals(userIdToUnfollow)) {
            return false; // Проверка на null и попытку отписаться от самого себя
        }

        Follower existingFollower = followerRepository.findByUser_UserIdAndFollower_UserId(userIdToUnfollow, currentUser.get().getUserId());
        if (existingFollower != null) {
            Optional<User> followingUser = userRepository.findByUsername(currentUsername);
            if (followingUser.isPresent()) {
                Optional<Profile> followingProfile = Optional.of(new Profile());
                followingProfile = profileRepository.findByUser_UserId(followingUser.get().getUserId());
                if (followingProfile.isPresent()){
                    followingProfile.get().setFollowingCount(followingProfile.get().getFollowingCount() - 1);
                    profileRepository.save(followingProfile.get());
                }
            }
            Optional<Profile> profileToBeFollowed = profileRepository.findByUser_UserId(userIdToUnfollow);
            if (profileToBeFollowed.isPresent()){
                profileToBeFollowed.get().setFollowersCount(profileToBeFollowed.get().getFollowersCount() - 1);
                profileRepository.save(profileToBeFollowed.get());
            }
            followerRepository.delete(existingFollower);
            return true;
        }
        return false;
    }

    public List<Profile> getFollowers(Long userId) {
        // Получаем список всех подписчиков для данного пользователя
        List<Follower> followerEntities = followerRepository.findByUser_UserId(userId);
        followerEntities = followerEntities.stream().sorted(Comparator.comparing(Follower::getFollowedAt).reversed()).toList();

        // Преобразуем каждый Follower в User и собираем в список
        return followerEntities.stream()
                .map(follower -> profileRepository.findByUser_UserId(follower.getFollower().getUserId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public List<Profile> getFollowing(Long userId) {
        List<Follower> followings = followerRepository.findByFollower_UserId(userId);
        return followings.stream()
                .map(following -> profileRepository.findByUser_UserId(following.getUser().getUserId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
