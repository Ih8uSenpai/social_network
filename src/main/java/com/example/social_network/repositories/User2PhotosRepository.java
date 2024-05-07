package com.example.social_network.repositories;

import com.example.social_network.entity.User;
import com.example.social_network.entity.User2Photos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface User2PhotosRepository extends JpaRepository<User2Photos, Long> {
    List<User2Photos> findAllByUser(User user);
}
