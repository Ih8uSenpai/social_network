package com.example.social_network.repositories;

import com.example.social_network.entity.Chat;
import com.example.social_network.entity.Message;
import com.example.social_network.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatId(Long chatId);

    Optional<Message> findFirstByChatIdOrderBySentAtDesc(Long chatId);


    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND not m.sender.user.userId = :userId AND m.messageId not in (select vm.messageId from ViewedMessage vm where vm.messageId = m.messageId and vm.userId = :userId)")
    List<Message> findUnviewedMessagesByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("SELECT DISTINCT m.chat FROM Message m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :substring, '%'))")
    List<Chat> findChatsByMessageContentContaining(@Param("substring") String substring);

    @Query("""
        SELECT m FROM Message m
        WHERE NOT EXISTS (
            SELECT 1 FROM ViewedMessage vm
            WHERE vm.messageId = m.messageId AND vm.userId = :userId
        )
    """)
    List<Message> findAllUnreadMessagesForUser(@Param("userId") Long userId);
}

