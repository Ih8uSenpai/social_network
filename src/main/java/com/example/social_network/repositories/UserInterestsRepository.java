package com.example.social_network.repositories;

import com.example.social_network.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInterestsRepository extends JpaRepository<UserInterest, Long> {

    List<UserInterest> findByUserId(Long userId);

    Optional<UserInterest> findByUserIdAndTag(Long userId, String tag);
}
