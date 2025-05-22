//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.social_network.payextractor.langchain4j;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import chat.giga.model.completion.ChatFunctionCallEnum;
import chat.giga.model.completion.CompletionResponse;
import com.example.social_network.payextractor.langchain4j.utils.GigaChatHelper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.internal.RetryUtils;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.DefaultChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.List;
import java.util.Objects;
import lombok.Generated;

public class GigaChatChatModel implements ChatLanguageModel, TokenCountEstimator {
    private final GigaChatClient client;
    private final Tokenizer tokenizer;
    private final Integer maxRetries;
    private final List<ChatModelListener> listeners;
    private final GigaChatChatRequestParameters defaultChatRequestParameters;

    public GigaChatChatModel(HttpClient apiHttpClient, AuthClient authClient, Integer readTimeout, Integer connectTimeout, String apiUrl, boolean logRequests, boolean logResponses, boolean verifySslCerts, Tokenizer tokenizer, Integer maxRetries, List<ChatModelListener> listeners, GigaChatChatRequestParameters defaultChatRequestParameters) {
        this.client = GigaChatClient.builder().apiHttpClient(apiHttpClient).apiUrl(apiUrl).authClient(authClient).connectTimeout(connectTimeout).readTimeout(readTimeout).logRequests(logRequests).logResponses(logResponses).verifySslCerts(verifySslCerts).build();
        this.tokenizer = tokenizer;
        this.maxRetries = (Integer)Utils.getOrDefault(maxRetries, 1);
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
        this.defaultChatRequestParameters = ((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)((GigaChatChatRequestParameters.GigaChatBuilder)GigaChatChatRequestParameters.builder().modelName(((ChatRequestParameters)commonParameters).modelName())).temperature(((ChatRequestParameters)commonParameters).temperature())).topP(((ChatRequestParameters)commonParameters).topP())).frequencyPenalty(((ChatRequestParameters)commonParameters).frequencyPenalty())).presencePenalty(((ChatRequestParameters)commonParameters).presencePenalty())).maxOutputTokens(((ChatRequestParameters)commonParameters).maxOutputTokens())).stopSequences(((ChatRequestParameters)commonParameters).stopSequences())).toolSpecifications(((ChatRequestParameters)commonParameters).toolSpecifications())).toolChoice(((ChatRequestParameters)commonParameters).toolChoice())).responseFormat(((ChatRequestParameters)commonParameters).responseFormat())).updateInterval((Integer)Utils.getOrDefault(gigaChatParameters.getUpdateInterval(), 0)).stream(false).profanityCheck((Boolean)Utils.getOrDefault(gigaChatParameters.getProfanityCheck(), false)).functionCall(Utils.getOrDefault(gigaChatParameters.getFunctionCall(), ChatFunctionCallEnum.AUTO)).attachments(gigaChatParameters.getAttachments()).repetitionPenalty(gigaChatParameters.getRepetitionPenalty()).build();
    }

    public ChatResponse doChat(ChatRequest chatRequest) {
        return GigaChatHelper.toResponse((CompletionResponse)RetryUtils.withRetry(() -> {
            return this.client.completions(GigaChatHelper.toRequest(chatRequest));
        }, this.maxRetries));
    }

    public List<ChatModelListener> listeners() {
        return this.listeners;
    }

    public int estimateTokenCount(List<ChatMessage> messages) {
        return this.tokenizer.estimateTokenCountInMessages(messages);
    }

    public GigaChatChatRequestParameters defaultRequestParameters() {
        return this.defaultChatRequestParameters;
    }

    @Generated
    public static GigaChatChatModelBuilder builder() {
        return new GigaChatChatModelBuilder();
    }

    @Generated
    public static class GigaChatChatModelBuilder {
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
        private Tokenizer tokenizer;
        @Generated
        private Integer maxRetries;
        @Generated
        private List<ChatModelListener> listeners;
        @Generated
        private GigaChatChatRequestParameters defaultChatRequestParameters;

        @Generated
        GigaChatChatModelBuilder() {
        }

        @Generated
        public GigaChatChatModelBuilder apiHttpClient(HttpClient apiHttpClient) {
            this.apiHttpClient = apiHttpClient;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder authClient(AuthClient authClient) {
            this.authClient = authClient;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder readTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder connectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder logRequests(boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder logResponses(boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder verifySslCerts(boolean verifySslCerts) {
            this.verifySslCerts = verifySslCerts;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder tokenizer(Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder listeners(List<ChatModelListener> listeners) {
            this.listeners = listeners;
            return this;
        }

        @Generated
        public GigaChatChatModelBuilder defaultChatRequestParameters(GigaChatChatRequestParameters defaultChatRequestParameters) {
            this.defaultChatRequestParameters = defaultChatRequestParameters;
            return this;
        }

        @Generated
        public GigaChatChatModel build() {
            return new GigaChatChatModel(this.apiHttpClient, this.authClient, this.readTimeout, this.connectTimeout, this.apiUrl, this.logRequests, this.logResponses, this.verifySslCerts, this.tokenizer, this.maxRetries, this.listeners, this.defaultChatRequestParameters);
        }

        @Generated
        public String toString() {
            String var10000 = String.valueOf(this.apiHttpClient);
            return "GigaChatChatModel.GigaChatChatModelBuilder(apiHttpClient=" + var10000 + ", authClient=" + String.valueOf(this.authClient) + ", readTimeout=" + this.readTimeout + ", connectTimeout=" + this.connectTimeout + ", apiUrl=" + this.apiUrl + ", logRequests=" + this.logRequests + ", logResponses=" + this.logResponses + ", verifySslCerts=" + this.verifySslCerts + ", tokenizer=" + String.valueOf(this.tokenizer) + ", maxRetries=" + this.maxRetries + ", listeners=" + String.valueOf(this.listeners) + ", defaultChatRequestParameters=" + String.valueOf(this.defaultChatRequestParameters) + ")";
        }
    }
}
