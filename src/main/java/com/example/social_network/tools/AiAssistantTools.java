package com.example.social_network.tools;

import com.example.social_network.entity.Message;
import com.example.social_network.repositories.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.social_network.controllers.AiChatController.currentUserId;

@Service
public class AiAssistantTools {
    private final MessageRepository messageRepository;

    public AiAssistantTools(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Tool("инструмент для поиска непрочитанных сообщений")
    public String getUnreadMessages() {
        List<Message> messages = messageRepository.findAllUnreadMessagesForUser(currentUserId);
        List<ReadableMessageDto> dtos = toReadableDto(messages);
        try {
            return objectMapper.writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public List<ReadableMessageDto> toReadableDto(List<Message> messages) {
        return messages.stream()
                .map(m -> {
                    var senderProfile = m.getSender();
                    return new ReadableMessageDto(
                            senderProfile.getFirstName(),
                            senderProfile.getLastName(),
                            senderProfile.getTag(),
                            m.getContent(),
                            m.getSentAt()
                    );
                })
                .collect(Collectors.toList());
    }

    public record ReadableMessageDto(
            String firstName,
            String lastName,
            String tag,
            String content,
            LocalDateTime sentAt
    ) {}

}
