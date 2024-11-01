package com.example.social_network.repositories;

import com.example.social_network.entity.UserInterest;
import com.example.social_network.entity.UserToUserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserToUserInterestRepository extends JpaRepository<UserToUserInterest, Long> {

    List<UserToUserInterest> findByUserId(Long userId);

    UserToUserInterest findByUserIdAndTargetUserId(Long userId, Long targetUserId);
}