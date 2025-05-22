//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.social_network.payextractor.langchain4j;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.internal.RetryUtils;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import java.util.Base64;
import java.util.List;
import lombok.Generated;

public class GigaChatImageModel implements ImageModel {
    private final GigaChatClient client;
    private final Integer maxRetries;
    private final GigaChatChatModel chatModel;

    public GigaChatImageModel(HttpClient apiHttpClient, AuthClient authClient, Integer readTimeout, Integer connectTimeout, String apiUrl, boolean logRequests, boolean logResponses, boolean verifySslCerts, Tokenizer tokenizer, Integer maxRetries, List<ChatModelListener> listeners, GigaChatChatRequestParameters defaultChatRequestParameters) {
        this.chatModel = GigaChatChatModel.builder().apiHttpClient(apiHttpClient).apiUrl(apiUrl).authClient(authClient).connectTimeout(connectTimeout).readTimeout(readTimeout).logRequests(logRequests).logResponses(logResponses).verifySslCerts(verifySslCerts).listeners(listeners).tokenizer(tokenizer).defaultChatRequestParameters(defaultChatRequestParameters).build();
        this.client = GigaChatClient.builder().apiHttpClient(apiHttpClient).apiUrl(apiUrl).authClient(authClient).connectTimeout(connectTimeout).readTimeout(readTimeout).logRequests(logRequests).logResponses(logResponses).verifySslCerts(verifySslCerts).build();
        this.maxRetries = (Integer)Utils.getOrDefault(maxRetries, 1);
    }

    public Response<Image> generate(String userMessage) {
        ChatResponse response = this.chatModel.chat(ChatRequest.builder().messages(new ChatMessage[]{UserMessage.from(userMessage)}).build());
        String completionsResponse = response.aiMessage().text();
        if (completionsResponse != null) {
            if (completionsResponse.contains("img src=")) {
                String fileId = completionsResponse.split("\"")[1];
                byte[] file = (byte[])RetryUtils.withRetry(() -> {
                    return this.client.downloadFile(fileId, (String)null);
                }, this.maxRetries);
                String base64FileData = new String(Base64.getEncoder().encode(file));
                return Response.from(Image.builder().base64Data(base64FileData).build(), response.tokenUsage());
            } else {
                throw new RuntimeException("No image was generated response does not contain 'img src='");
            }
        } else {
            throw new RuntimeException("No image was generated response is null");
        }
    }

    @Generated
    public static GigaChatImageModelBuilder builder() {
        return new GigaChatImageModelBuilder();
    }

    @Generated
    public static class GigaChatImageModelBuilder {
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
        GigaChatImageModelBuilder() {
        }

        @Generated
        public GigaChatImageModelBuilder apiHttpClient(HttpClient apiHttpClient) {
            this.apiHttpClient = apiHttpClient;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder authClient(AuthClient authClient) {
            this.authClient = authClient;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder readTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder connectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder logRequests(boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder logResponses(boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder verifySslCerts(boolean verifySslCerts) {
            this.verifySslCerts = verifySslCerts;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder tokenizer(Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder listeners(List<ChatModelListener> listeners) {
            this.listeners = listeners;
            return this;
        }

        @Generated
        public GigaChatImageModelBuilder defaultChatRequestParameters(GigaChatChatRequestParameters defaultChatRequestParameters) {
            this.defaultChatRequestParameters = defaultChatRequestParameters;
            return this;
        }

        @Generated
        public GigaChatImageModel build() {
            return new GigaChatImageModel(this.apiHttpClient, this.authClient, this.readTimeout, this.connectTimeout, this.apiUrl, this.logRequests, this.logResponses, this.verifySslCerts, this.tokenizer, this.maxRetries, this.listeners, this.defaultChatRequestParameters);
        }

        @Generated
        public String toString() {
            String var10000 = String.valueOf(this.apiHttpClient);
            return "GigaChatImageModel.GigaChatImageModelBuilder(apiHttpClient=" + var10000 + ", authClient=" + String.valueOf(this.authClient) + ", readTimeout=" + this.readTimeout + ", connectTimeout=" + this.connectTimeout + ", apiUrl=" + this.apiUrl + ", logRequests=" + this.logRequests + ", logResponses=" + this.logResponses + ", verifySslCerts=" + this.verifySslCerts + ", tokenizer=" + String.valueOf(this.tokenizer) + ", maxRetries=" + this.maxRetries + ", listeners=" + String.valueOf(this.listeners) + ", defaultChatRequestParameters=" + String.valueOf(this.defaultChatRequestParameters) + ")";
        }
    }
}
