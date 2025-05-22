package com.example.social_network.payextractor.Assistants;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {
    @SystemMessage("""
        Отвечай на все вопросы пользователя.если он попросит что-то выполнить то проверь инструменты которые у тебя есть,
        если есть подходящий то выполни это действие и сообщи пользователю результат.
        """)
    String chat(@MemoryId String memoryId, @UserMessage String message);
}