package com.example.social_network.repositories;

import com.example.social_network.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    List<ChatUser> findByUserId(Long userId);
    List<ChatUser> findByChatId(Long chatId);
}
