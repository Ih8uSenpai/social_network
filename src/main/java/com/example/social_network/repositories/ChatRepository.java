package com.example.social_network.repositories;

import com.example.social_network.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.chatType = com.example.social_network.utils.ChatType.PRIVATE " +
            "AND EXISTS (SELECT cu1 FROM ChatUser cu1 WHERE cu1.chatId = c.id AND cu1.userId = :userId1) " +
            "AND EXISTS (SELECT cu2 FROM ChatUser cu2 WHERE cu2.chatId = c.id AND cu2.userId = :userId2) " +
            "AND (SELECT COUNT(cu.userId) FROM ChatUser cu WHERE cu.chatId = c.id) = 2")
    Optional<Chat> findPrivateChatBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}