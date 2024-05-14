package com.example.social_network.repositories;

import com.example.social_network.entity.ViewedMessage;
import com.example.social_network.entity.ViewedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewedMessageRepository extends JpaRepository<ViewedMessage, Long> {
    boolean existsByUserIdAndMessageId(Long userId, Long messageId);
    boolean existsByMessageId(Long messageId);
}
