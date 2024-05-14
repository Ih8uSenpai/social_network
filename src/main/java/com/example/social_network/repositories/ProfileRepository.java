package com.example.social_network.repositories;

import com.example.social_network.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser_UserId(Long userId);
    List<Profile> findByTagContainingIgnoreCase(String tag);

}
