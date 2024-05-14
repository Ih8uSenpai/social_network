package com.example.social_network.repositories;

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


    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND not m.sender.userId = :userId AND m.messageId not in (select vm.messageId from ViewedMessage vm where vm.messageId = m.messageId and vm.userId = :userId)")
    List<Message> findUnviewedMessagesByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);
}

