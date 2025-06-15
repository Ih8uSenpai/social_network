package com.example.social_network.config;

import chat.giga.langchain4j.GigaChatChatModel;
import com.example.social_network.payextractor.Assistants.Assistant;
import com.example.social_network.tools.AiAssistantTools;
import com.example.social_network.utils.TagExtractor;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    private final GigaChatChatModel gigaChatChatModel;
    private final AiAssistantTools aiAssistantTools;

    public AppConfig(GigaChatChatModel gigaChatChatModel, AiAssistantTools aiAssistantTools) {
        this.gigaChatChatModel = gigaChatChatModel;
        this.aiAssistantTools = aiAssistantTools;
    }

    @Bean
    public TagExtractor tagExtractor() {
        return new TagExtractor();
    }

    @Bean
    public Assistant assistant() {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(gigaChatChatModel)
                .tools(aiAssistantTools)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().maxMessages(10).build())
                .build();
    }
}
