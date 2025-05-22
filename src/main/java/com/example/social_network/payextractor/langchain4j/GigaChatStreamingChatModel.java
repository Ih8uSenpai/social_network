//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.social_network.payextractor.langchain4j;

import chat.giga.client.GigaChatClientAsync;
import chat.giga.client.ResponseHandler;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import chat.giga.model.completion.ChatFunctionCallEnum;
import chat.giga.model.completion.ChoiceFinishReason;
import chat.giga.model.completion.CompletionChunkResponse;
import com.example.social_network.payextractor.langchain4j.utils.GigaChatHelper;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.DefaultChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.ChatResponseMetadata;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Generated;

public class GigaChatStreamingChatModel implements StreamingChatLanguageModel {
    private final GigaChatClientAsync asyncClient;
    private final List<ChatModelListener> listeners;
    private final GigaChatChatRequestParameters defaultChatRequestParameters;

    public GigaChatStreamingChatModel(HttpClient apiHttpClient, AuthClient authClient, Integer readTimeout, Integer connectTimeout, String apiUrl, boolean logRequests, boolean logResponses, boolean verifySslCerts, List<ChatModelListener> listeners, GigaChatChatRequestParameters defaultChatRequestParameters) {
        this.asyncClient = GigaChatClientAsync.builder().apiHttpClient(apiHttpClient).apiUrl(apiUrl).authClient(authClient).connectTimeout(connectTimeout).readTimeout(readTimeout).logRequests(logRequests).logResponses(logResponses).verifySslCerts(verifySslCerts).build();
        this.listeners = listeners;
        Object commonParameters;
        if (defaultChatRequestParameters != null) {
            commonParameters = defaultChatRequestParameters;
        } else {
            commonParameters = DefaultChatRequestParameters.builder().build();
        }

        GigaChatChatRequestParameters gigaChatParameters;
        if (defaultChatRequestParameters != null) {
            gigaChatParameters = defaultChatRequestParameters;
        } else {
            gigaChatParameters = GigaChatChatRequestParameters.builder().build();
        }

        Objects.requireNonNull(((ChatRequestParameters)commonParameters).modelName(), "Model name must not be null");
        this.defaultChatRequestParameters = ((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)GigaChatChatRequestParameters.builder().modelName(((ChatRequestParameters)commonParameters).modelName())).temperature(((ChatRequestParameters)commonParameters).temperature())).topP(((ChatRequestParameters)commonParameters).topP())).frequencyPenalty(((ChatRequestParameters)commonParameters).frequencyPenalty())).presencePenalty(((ChatRequestParameters)commonParameters).presencePenalty())).maxOutputTokens(((ChatRequestParameters)commonParameters).maxOutputTokens())).stopSequences(((ChatRequestParameters)commonParameters).stopSequences())).toolSpecifications(((ChatRequestParameters)commonParameters).toolSpecifications())).toolChoice(((ChatRequestParameters)commonParameters).toolChoice())).responseFormat(((ChatRequestParameters)commonParameters).responseFormat())).updateInterval((Integer)Utils.getOrDefault(gigaChatParameters.getUpdateInterval(), 0)).stream(true).profanityCheck((Boolean)Utils.getOrDefault(gigaChatParameters.getProfanityCheck(), false)).functionCall(Utils.getOrDefault(gigaChatParameters.getFunctionCall(), ChatFunctionCallEnum.AUTO)).attachments(gigaChatParameters.getAttachments()).repetitionPenalty(gigaChatParameters.getRepetitionPenalty()).build();
    }

    public void doChat(ChatRequest chatRequest, final StreamingChatResponseHandler handler) {
        final ChatResponseMetadata.Builder<? extends ChatResponseMetadata.Builder<?>> responseMetadataBuilder = ChatResponseMetadata.builder();
        final StringBuffer text = new StringBuffer();
        final ArrayList<ToolExecutionRequest> toolExecutionRequests = new ArrayList();

        try {
            this.asyncClient.completions(GigaChatHelper.toRequest(chatRequest), new ResponseHandler<CompletionChunkResponse>() {
                public void onNext(CompletionChunkResponse completionChunkResponse) {
                    GigaChatStreamingChatModel.this.handlePartialResponse(completionChunkResponse, handler, responseMetadataBuilder, text, toolExecutionRequests);
                }

                public void onComplete() {
                    AiMessage aiMessage;
                    if (!text.toString().isEmpty()) {
                        if (!toolExecutionRequests.isEmpty()) {
                            aiMessage = AiMessage.from(text.toString(), toolExecutionRequests);
                        } else {
                            aiMessage = AiMessage.from(text.toString());
                        }
                    } else {
                        if (toolExecutionRequests.isEmpty()) {
                            throw new IllegalArgumentException("No text or toolExecutionRequests found in the response");
                        }

                        aiMessage = AiMessage.from(toolExecutionRequests);
                    }

                    ChatResponse chatResponse = ChatResponse.builder().aiMessage(aiMessage).metadata(responseMetadataBuilder.build()).build();
                    handler.onCompleteResponse(chatResponse);
                }

                public void onError(Throwable throwable) {
                    handler.onError(throwable);
                }
            });
        } catch (Exception var7) {
            handler.onError(var7);
        }

    }

    private void handlePartialResponse(CompletionChunkResponse chatCompletionChunk, StreamingChatResponseHandler handler, ChatResponseMetadata.Builder responseMetadataBuilder, StringBuffer text, List<ToolExecutionRequest> toolExecutionRequests) {
        responseMetadataBuilder.modelName(chatCompletionChunk.model());
        chatCompletionChunk.choices().forEach((choice) -> {
            if (choice.delta().content() != null) {
                text.append(choice.delta().content());
                handler.onPartialResponse(choice.delta().content());
            }

            if (choice.finishReason() == ChoiceFinishReason.FUNCTION_CALL) {
                toolExecutionRequests.add(GigaChatHelper.toToolExecutionRequest(choice));
            }

            if (choice.finishReason() != null) {
                responseMetadataBuilder.finishReason(GigaChatHelper.finishReasonFrom(choice.finishReason().value()));
            }

        });
        if (chatCompletionChunk.usage() != null) {
            responseMetadataBuilder.tokenUsage(GigaChatHelper.toTokenUsage(chatCompletionChunk.usage()));
        }

    }

    public List<ChatModelListener> listeners() {
        return this.listeners;
    }

    public DefaultChatRequestParameters defaultRequestParameters() {
        return this.defaultChatRequestParameters;
    }

    @Generated
    public static GigaChatStreamingChatModelBuilder builder() {
        return new GigaChatStreamingChatModelBuilder();
    }

    @Generated
    public static class GigaChatStreamingChatModelBuilder {
        @Generated
        private HttpClient apiHttpClient;
        @Generated
        private AuthClient authClient;
        @Generated
        private Integer readTimeout;
        @Generated
        private Integer connectTimeout;
        @Generated
        private String apiUrl;
        @Generated
        private boolean logRequests;
        @Generated
        private boolean logResponses;
        @Generated
        private boolean verifySslCerts;
        @Generated
        private List<ChatModelListener> listeners;
        @Generated
        private GigaChatChatRequestParameters defaultChatRequestParameters;

        @Generated
        GigaChatStreamingChatModelBuilder() {
        }

        @Generated
        public GigaChatStreamingChatModelBuilder apiHttpClient(HttpClient apiHttpClient) {
            this.apiHttpClient = apiHttpClient;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder authClient(AuthClient authClient) {
            this.authClient = authClient;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder readTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder connectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder logRequests(boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder logResponses(boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder verifySslCerts(boolean verifySslCerts) {
            this.verifySslCerts = verifySslCerts;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder listeners(List<ChatModelListener> listeners) {
            this.listeners = listeners;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModelBuilder defaultChatRequestParameters(GigaChatChatRequestParameters defaultChatRequestParameters) {
            this.defaultChatRequestParameters = defaultChatRequestParameters;
            return this;
        }

        @Generated
        public GigaChatStreamingChatModel build() {
            return new GigaChatStreamingChatModel(this.apiHttpClient, this.authClient, this.readTimeout, this.connectTimeout, this.apiUrl, this.logRequests, this.logResponses, this.verifySslCerts, this.listeners, this.defaultChatRequestParameters);
        }

        @Generated
        public String toString() {
            String var10000 = String.valueOf(this.apiHttpClient);
            return "GigaChatStreamingChatModel.GigaChatStreamingChatModelBuilder(apiHttpClient=" + var10000 + ", authClient=" + String.valueOf(this.authClient) + ", readTimeout=" + this.readTimeout + ", connectTimeout=" + this.connectTimeout + ", apiUrl=" + this.apiUrl + ", logRequests=" + this.logRequests + ", logResponses=" + this.logResponses + ", verifySslCerts=" + this.verifySslCerts + ", listeners=" + String.valueOf(this.listeners) + ", defaultChatRequestParameters=" + String.valueOf(this.defaultChatRequestParameters) + ")";
        }
    }
}
