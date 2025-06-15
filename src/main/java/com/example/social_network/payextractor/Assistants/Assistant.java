package com.example.social_network.payextractor.Assistants;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {
    @SystemMessage("""
        Отвечай на все вопросы пользователя.
        Инструменты, которыми ты можешь пользоваться.
        1. инструмент для получения всех непрочитанных сообщений пользователя, выводи от кого это сообщение, его содержание и время отправки в удобном виде, с форматированием.
        """)
    String chat(@MemoryId String memoryId, @UserMessage String message);
}