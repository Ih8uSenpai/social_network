package com.example.social_network.payextractor.Assistants;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {
    @SystemMessage("""
        Отвечай на все вопросы пользователя.
        """)
    String chat(@MemoryId String memoryId, @UserMessage String message);
}