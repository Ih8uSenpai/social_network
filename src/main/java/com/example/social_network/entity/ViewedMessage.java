package com.example.social_network.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "viewed_messages")
@Getter
@Setter
@RequiredArgsConstructor
public class ViewedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "message_id")
    private Long messageId;

    public ViewedMessage(Long userId, Long messageId) {
        this.userId = userId;
        this.messageId = messageId;
    }
}
