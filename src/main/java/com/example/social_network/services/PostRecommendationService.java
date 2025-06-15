package com.example.social_network.services;

import com.example.social_network.entity.Post;
import com.example.social_network.entity.UserInterest;
import com.example.social_network.entity.UserToUserInterest;
import com.example.social_network.entity.ViewedPost;
import com.example.social_network.repositories.PostRepository;
import com.example.social_network.repositories.UserInterestsRepository;
import com.example.social_network.repositories.UserToUserInterestRepository;
import com.example.social_network.repositories.ViewedPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostRecommendationService {

    private final PostRepository postRepository;
    private final UserInterestsRepository userInterestsRepository;
    private final UserToUserInterestRepository userToUserInterestRepository;
    private final ViewedPostRepository viewedPostRepository;

    private static final int NEWS_FEED_SIZE = 20;


    public List<Post> recommendPosts(Long userId) {
        // Шаг 1: Получаем интересы пользователя
        List<UserInterest> userInterests = userInterestsRepository.findByUserId(userId);

        // Шаг 2: Если нет интересов, возвращаем популярные посты
        if (userInterests.isEmpty()) {
            System.out.println("\n\n\n\n\n\n\n\nUSER INTEREST EMPTY");
            return getPopularPosts(userId);
        }

        // Шаг 3: Получаем теги с наивысшими интересами
        Set<String> topInterests = userInterests.stream()
                .sorted(Comparator.comparingDouble(UserInterest::getInterestScore).reversed())
                .limit(10) // Лимитируем количество топ интересов, например 5
                .map(UserInterest::getTag)
                .collect(Collectors.toSet());

        // Шаг 4: Получаем список ID уже просмотренных постов
        Set<Long> viewedPostIds = viewedPostRepository.findByUserId(userId).stream()
                .map(ViewedPost::getPostId)
                .collect(Collectors.toSet());

        Set<Long> addedPostIds = new HashSet<>();

        // Шаг 5: Находим посты, которые соответствуют интересам пользователя и не были просмотрены
        List<Post> recommendedPosts = postRepository.findByTagsIn(topInterests, userId).stream()
                .filter(post -> !viewedPostIds.contains(post.getId()) && addedPostIds.add(post.getId()))  // Исключаем уже просмотренные и дублирующиеся посты
                .sorted(Comparator.comparingInt(post -> getPostMatchScore(post, topInterests)))
                .limit(NEWS_FEED_SIZE)
                .collect(Collectors.toList());

        // Шаг 6: Если недостаточно постов, добавляем из постов которые понравились бы другим пользователям, в которых заинтересован текущий пользователь
        if (recommendedPosts.size() < NEWS_FEED_SIZE) {
            Set<String> topInterests2 = getTopTagsByCombinedInterest(userId, 10);
            List<Post> recommendedPosts2 = postRepository.findByTagsIn(topInterests2, userId).stream()
                    .filter(post -> !viewedPostIds.contains(post.getId()) && addedPostIds.add(post.getId()))  // Исключаем уже просмотренные и дублирующиеся посты
                    .sorted(Comparator.comparingInt(post -> getPostMatchScore(post, topInterests2)))
                    .limit(NEWS_FEED_SIZE - recommendedPosts.size()).toList();
            recommendedPosts.addAll(recommendedPosts2);
        }

        // Шаг 7: Если все еще недостаточно постов, добавляем новые или популярные
        if (recommendedPosts.size() < NEWS_FEED_SIZE) {
            return recommendedPosts.stream()
                    .distinct()
                    .limit(NEWS_FEED_SIZE)
                    .collect(Collectors.toList());
        }

        return recommendedPosts;
    }

    // Метод для вычисления топовых тегов интересов пользователя по интересам его топ-5 пользователей
    public Set<String> getTopTagsByCombinedInterest(Long userId, int topN) {
        // Шаг 1: Находим топ-5 пользователей, которые больше всего интересуют данного пользователя
        List<UserToUserInterest> topUserInterests = userToUserInterestRepository.findByUserId(userId)
                .stream()
                .sorted(Comparator.comparingDouble(UserToUserInterest::getInterestScore).reversed())
                .limit(5).toList();

        // Шаг 2: Собираем все теги интересов этих топ-5 пользователей и рассчитываем общий интерес
        Map<String, Double> combinedInterests = new HashMap<>();

        for (UserToUserInterest interest : topUserInterests) {
            Long targetUserId = interest.getTargetUserId();
            List<UserInterest> targetUserInterests = userInterestsRepository.findByUserId(targetUserId);

            // Для каждого тега добавляем или обновляем общий интерес
            for (UserInterest targetInterest : targetUserInterests) {
                combinedInterests.merge(
                        targetInterest.getTag(),
                        targetInterest.getInterestScore() * interest.getInterestScore(),
                        Double::sum
                );
            }
        }

        // Шаг 3: Сортируем теги по убыванию интереса и возвращаем топ-N тегов
        return combinedInterests.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private List<Post> getPopularPosts(Long userId) {
        Set<Long> viewedPostIds = viewedPostRepository.findByUserId(userId).stream()
                .map(ViewedPost::getPostId)
                .collect(Collectors.toSet());

        return postRepository.findPopularPosts().stream()
                .filter(post -> !viewedPostIds.contains(post.getId()))  // Исключаем просмотренные посты
                .limit(NEWS_FEED_SIZE)
                .collect(Collectors.toList());
    }

    private List<Post> getNewOrPopularPosts(Long userId, Set<Long> viewedPostIds, int limit) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        int minLikes = 10;

        return postRepository.findNewOrPopularPosts(oneMonthAgo, minLikes).stream()
                .filter(post -> !viewedPostIds.contains(post.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Подсчет соответствия поста интересам пользователя
    private int getPostMatchScore(Post post, Set<String> topInterests) {
        return (int) post.getTags().stream()
                .filter(topInterests::contains)
                .count();
    }
}
