package com.example.social_network.payextractor.Assistants;


import com.example.social_network.payextractor.langchain4j.GigaChatChatModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс-реестр для хранения экземпляров Assistant, привязанных к sessionId (или user).
 */
@Component
@NoArgsConstructor
public class AssistantRegistry {
    private final Map<String, Assistant> assistants = new ConcurrentHashMap<>();
    private ContentRetriever sharedContentRetriever;
    private EmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;
    private GigaChatChatModel gigaChatChatModel;

    public AssistantRegistry(EmbeddingModel embeddingModel, GigaChatChatModel gigaChatChatModel) {
        this.embeddingModel = embeddingModel;
        this.gigaChatChatModel = gigaChatChatModel;

    }



    public Assistant getOrCreateAssistant(String sessionId) {
        return assistants.computeIfAbsent(sessionId, id -> createAssistant());
    }

    /**
     * Метод для получения содержимого RAG для конкретного запроса
     * Возвращает контент, найденный для запроса
     */

    /**
     * Выводит содержимое извлеченных сегментов для запроса
     */


    private Assistant createAssistant() {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(gigaChatChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().maxMessages(10).build())
                .build();
    }
}