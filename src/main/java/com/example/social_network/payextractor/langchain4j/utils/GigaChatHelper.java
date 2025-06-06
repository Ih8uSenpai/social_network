//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.social_network.payextractor.langchain4j.utils;

import chat.giga.model.completion.ChatFunction;
import chat.giga.model.completion.ChatFunctionParameters;
import chat.giga.model.completion.ChatFunctionParametersProperty;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChoiceChunk;
import chat.giga.model.completion.ChoiceMessageFunctionCall;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.completion.Usage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.util.JsonUtils;
import com.example.social_network.payextractor.langchain4j.GigaChatChatRequestParameters;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema;
import dev.langchain4j.model.chat.request.json.JsonNumberSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.ChatResponseMetadata;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GigaChatHelper {
    public GigaChatHelper() {
    }

    private static List<ChatMessage> convertChatMessages(List<dev.langchain4j.data.message.ChatMessage> messages, GigaChatChatRequestParameters parameters) {
        return (List)messages.stream().map((message) -> {
            return convertMessage(message, parameters);
        }).collect(Collectors.toList());
    }

    private static ChatMessage convertMessage(dev.langchain4j.data.message.ChatMessage message, GigaChatChatRequestParameters parameters) {
        if (message instanceof UserMessage) {
            return ChatMessage.builder().role(Role.USER).content((String)((UserMessage)message).contents().stream().map((content) -> {
                return content instanceof TextContent ? ((TextContent)content).text() : null;
            }).toList().get(0)).attachments(Utils.getOrDefault(parameters.getAttachments(), List.of())).build();
        } else if (message instanceof SystemMessage) {
            return ChatMessage.builder().role(Role.SYSTEM).content(((SystemMessage)message).text()).build();
        } else if (message instanceof AiMessage aiMessage) {
            if (aiMessage.toolExecutionRequests() != null && !aiMessage.toolExecutionRequests().isEmpty()) {
                return chat.giga.model.completion.ChatMessage.builder()
                        .role(ChatMessage.Role.ASSISTANT)
                        .functionsStateId(aiMessage.toolExecutionRequests().get(0).id())
                        .content(aiMessage.text())
                        .build();
            } else {
                return chat.giga.model.completion.ChatMessage.builder()
                        .role(ChatMessage.Role.ASSISTANT)
                        .content(aiMessage.text())
                        .build();
            }
        } else if (message instanceof ToolExecutionResultMessage) {
            return ChatMessage.builder().role(Role.FUNCTION).content(((ToolExecutionResultMessage)message).text()).build();
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + message.getClass().getName());
        }
    }

    private static Map<String, ChatFunctionParametersProperty> convertParameters(Map<String, JsonSchemaElement> inputMap) {
        return (Map)inputMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> {
            return convertToChatFunctionParametersProperty((JsonSchemaElement)entry.getValue());
        }));
    }

    private static ChatFunctionParametersProperty convertToChatFunctionParametersProperty(JsonSchemaElement schemaElement) {
        String type = "string";
        if (schemaElement instanceof JsonObjectSchema) {
            return ChatFunctionParametersProperty.builder().type("object").properties(convertParameters(((JsonObjectSchema)schemaElement).properties())).description(((JsonObjectSchema)schemaElement).description()).build();
        } else if (schemaElement instanceof JsonStringSchema) {
            return ChatFunctionParametersProperty.builder().type(type).description(((JsonStringSchema)schemaElement).description()).build();
        } else if (schemaElement instanceof JsonIntegerSchema) {
            return ChatFunctionParametersProperty.builder().type(type).description(((JsonIntegerSchema)schemaElement).description()).build();
        } else {
            return schemaElement instanceof JsonNumberSchema ? ChatFunctionParametersProperty.builder().type(type).description(((JsonNumberSchema)schemaElement).description()).build() : ChatFunctionParametersProperty.builder().build();
        }
    }

    public static ChatResponse toResponse(CompletionResponse completions) {
        return (ChatResponse)completions.choices().stream().map((s) -> {
            ToolExecutionRequest toolExecutionRequest = null;
            if (s.message().functionCall() != null) {
                String args = toArgumentsString(s.message().functionCall());
                toolExecutionRequest = ToolExecutionRequest.builder().id(s.message().functionsStateId()).name(s.message().functionCall().name()).arguments(args).build();
            }

            AiMessage aiMessage = toolExecutionRequest != null ? AiMessage.builder().text(s.message().content()).toolExecutionRequests(Collections.singletonList(toolExecutionRequest)).build() : AiMessage.builder().text(s.message().content()).build();
            return ChatResponse.builder().aiMessage(aiMessage).metadata(ChatResponseMetadata.builder().modelName(completions.model()).tokenUsage(new TokenUsage(completions.usage().promptTokens(), completions.usage().completionTokens(), completions.usage().totalTokens())).finishReason(finishReasonFrom(s.finishReason() != null ? s.finishReason().value() : null)).build()).build();
        }).findAny().orElseThrow(() -> {
            return new IllegalArgumentException("Choices is empty in the response");
        });
    }

    public static ToolExecutionRequest toToolExecutionRequest(ChoiceChunk choice) {
        ChoiceMessageFunctionCall function = choice.delta().functionCall();
        String functionId = null;
        String functionName = null;
        String functionArguments = null;
        if (choice.delta().functionsStateId() != null) {
            functionId = choice.delta().functionsStateId();
        }

        if (function.name() != null) {
            functionName = function.name();
        }

        if (function.arguments() != null && !function.arguments().isEmpty()) {
            functionArguments = toArgumentsString(function);
        }

        return ToolExecutionRequest.builder().id(functionId).name(functionName).arguments(functionArguments).build();
    }

    private static String toArgumentsString(ChoiceMessageFunctionCall function) {
        return ((JsonNode)JsonUtils.objectMapper().convertValue(function.arguments(), JsonNode.class)).toString();
    }

    public static TokenUsage toTokenUsage(Usage usage) {
        return new TokenUsage(usage.promptTokens(), usage.completionTokens(), usage.totalTokens());
    }

    public static CompletionRequest toRequest(ChatRequest chatRequest) {
        GigaChatChatRequestParameters parameters = (GigaChatChatRequestParameters)chatRequest.parameters();
        return CompletionRequest.builder().model(chatRequest.parameters().modelName()).messages(convertChatMessages(chatRequest.messages(), parameters)).temperature(chatRequest.parameters().temperature() != null ? chatRequest.parameters().temperature().floatValue() : null).topP(chatRequest.parameters().topP() != null ? chatRequest.parameters().topP().floatValue() : null).maxTokens(chatRequest.parameters().maxOutputTokens()).repetitionPenalty(parameters.getRepetitionPenalty()).profanityCheck(parameters.getProfanityCheck()).stream(parameters.getStream()).updateInterval(parameters.getUpdateInterval()).functionCall(parameters.getFunctionCall()).functions((Collection)(chatRequest.toolSpecifications() != null ? (Collection)chatRequest.toolSpecifications().stream().map((toolSpecification) -> {
            ChatFunctionParameters chatFunctionParameters = ChatFunctionParameters.builder().required(toolSpecification.parameters().required()).properties(convertParameters(toolSpecification.parameters().properties())).build();
            return ChatFunction.builder().name(toolSpecification.name()).description(toolSpecification.description()).parameters(chatFunctionParameters).build();
        }).collect(Collectors.toList()) : List.of())).build();
    }

    public static FinishReason finishReasonFrom(String reason) {
        if (reason == null) {
            return null;
        } else {
            FinishReason var10000;
            switch (reason) {
                case "stop":
                    var10000 = FinishReason.STOP;
                    break;
                case "length":
                    var10000 = FinishReason.LENGTH;
                    break;
                case "function_call":
                    var10000 = FinishReason.TOOL_EXECUTION;
                    break;
                case "content_filter":
                    var10000 = FinishReason.CONTENT_FILTER;
                    break;
                default:
                    var10000 = null;
            }

            return var10000;
        }
    }
}
