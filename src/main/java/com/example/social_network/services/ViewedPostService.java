package com.example.social_network.services;

import com.example.social_network.entity.Post;
import com.example.social_network.entity.ViewedPost;
import com.example.social_network.repositories.ViewedPostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ViewedPostService {

    private final ViewedPostRepository viewedPostRepository;

    private final UserInterestsService userInterestsService;

    public ViewedPostService(ViewedPostRepository viewedPostRepository, UserInterestsService userInterestsService) {
        this.viewedPostRepository = viewedPostRepository;
        this.userInterestsService = userInterestsService;
    }

    @Transactional
    public void markPostAsViewed(Post post, ViewedPost viewedPost) {
        Optional<ViewedPost> viewedPostOptional = viewedPostRepository.findByUserIdAndPostId(viewedPost.getUserId(), viewedPost.getPostId());
        if (viewedPostOptional.isEmpty()) {
            viewedPostRepository.save(viewedPost);
            userInterestsService.viewPost(viewedPost.getUserId(), post, viewedPost.getViewType());
        } // if post was viewed we still can upgrade view type value
        else if (viewedPostOptional.get().getViewType() != ViewedPost.ViewType.LONG &&
        viewedPost.getViewType() != ViewedPost.ViewType.QUICK){
            // we should firstly remove old value to prevent stacking
            userInterestsService.removeViewPost(viewedPost.getUserId(), post, viewedPostOptional.get().getViewType());

            viewedPostOptional.get().setViewType(viewedPost.getViewType());
            viewedPostRepository.save(viewedPostOptional.get());
            userInterestsService.viewPost(viewedPost.getUserId(), post, viewedPost.getViewType());
        }
    }
}
