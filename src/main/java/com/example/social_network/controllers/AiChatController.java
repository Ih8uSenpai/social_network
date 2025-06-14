package com.example.social_network.controllers;


import chat.giga.langchain4j.GigaChatChatModel;
import com.example.social_network.payextractor.Assistants.Assistant;
import com.example.social_network.payextractor.Assistants.AssistantRegistry;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@CrossOrigin(origins = "${FRONTEND_URL}")
public class AiChatController {

    private final AssistantRegistry assistantRegistry;
    private final GigaChatChatModel gigaChatChatModel;
    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);

    // Блокировка для предотвращения одновременного доступа к памяти одного sessionId.
    private final Map<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public AiChatController(AssistantRegistry assistantRegistry, GigaChatChatModel gigaChatChatModel) {
        this.assistantRegistry = assistantRegistry;
        this.gigaChatChatModel = gigaChatChatModel;
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
                       @RequestParam String question) {

        ReentrantLock lock = getLockForUser(sessionId);
        lock.lock(); // Получаем блокировку для данного sessionId
        try {
            // Получаем (или создаём) экземпляр Assistant, соответствующий sessionId
            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(gigaChatChatModel)
                    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().maxMessages(10).build())
                    .build();
            String response = assistant.chat(sessionId, question);
            logger.info("Session: {}, Question: {}, Response: {}", sessionId, question, response);
            return response;
        } finally {
            lock.unlock();
        }
    }
}
