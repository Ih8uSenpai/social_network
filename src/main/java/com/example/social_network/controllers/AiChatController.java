package com.example.social_network.controllers;


import chat.giga.langchain4j.GigaChatChatModel;
import com.example.social_network.entity.User;
import com.example.social_network.payextractor.Assistants.Assistant;
import com.example.social_network.payextractor.Assistants.AssistantRegistry;
import com.example.social_network.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@CrossOrigin(origins = "${FRONTEND_URL}")
public class AiChatController {

    private final AssistantRegistry assistantRegistry;
    private final GigaChatChatModel gigaChatChatModel;
    private final Assistant assistant;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);

    //жеский костыль потом заменить на решение с concurrentHashMap
    public static Long currentUserId;

    // Блокировка для предотвращения одновременного доступа к памяти одного sessionId.
    private final Map<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public AiChatController(AssistantRegistry assistantRegistry, GigaChatChatModel gigaChatChatModel, Assistant assistant, UserRepository userRepository) {
        this.assistantRegistry = assistantRegistry;
        this.gigaChatChatModel = gigaChatChatModel;
        this.assistant = assistant;
        this.userRepository = userRepository;
    }


    private ReentrantLock getLockForUser(String sessionId) {
        return userLocks.computeIfAbsent(sessionId, k -> new ReentrantLock());
    }

    /**
     * Эндпоинт чата.
     * @param sessionId идентификатор сессии или пользователя, который соответствует памяти
     * @param question вопрос от клиента
     * @return ответ Assistant, сохранивший историю чата
     */
    @GetMapping("/{sessionId}/chat")
    public String chat(@PathVariable("sessionId") String sessionId,
                       @RequestParam String question, Principal principal) {


        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // жеский костыль потом заменить на решение с concurrentHashMap
        currentUserId = user.getUserId();

        ReentrantLock lock = getLockForUser(sessionId);
        lock.lock(); // Получаем блокировку для данного sessionId
        try {
            // Получаем (или создаём) экземпляр Assistant, соответствующий sessionId

            String response = assistant.chat(sessionId, question);
            logger.info("Session: {}, Question: {}, Response: {}", sessionId, question, response);
            return response;
        } finally {
            lock.unlock();
        }
    }
}
