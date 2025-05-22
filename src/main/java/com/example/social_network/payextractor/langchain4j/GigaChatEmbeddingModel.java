//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.social_network.payextractor.langchain4j;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.embedding.DimensionAwareEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;

public class GigaChatEmbeddingModel extends DimensionAwareEmbeddingModel {
    private final GigaChatClient gigaChatClient;
    private final String modelName;
    private final Integer batchSize;

    public GigaChatEmbeddingModel(HttpClient apiHttpClient, AuthClient authClient, Integer readTimeout, Integer connectTimeout, String apiUrl, boolean logRequests, boolean logResponses, boolean verifySslCerts, String modelName, Integer batchSize) {
        this.gigaChatClient = GigaChatClient.builder().apiHttpClient(apiHttpClient).apiUrl(apiUrl).authClient(authClient).connectTimeout(connectTimeout).readTimeout(readTimeout).logRequests(logRequests).logResponses(logResponses).verifySslCerts(verifySslCerts).build();
        this.modelName = (String)Utils.getOrDefault(modelName, "Embeddings");
        this.batchSize = (Integer)Utils.getOrDefault(batchSize, 16);
    }

    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        List<String> texts = (List)textSegments.stream().map(TextSegment::text).collect(Collectors.toList());
        return this.embedTexts(texts);
    }

    private Response<List<Embedding>> embedTexts(List<String> texts) {
        ArrayList<Embedding> embeddings = new ArrayList();
        int inputTokenCount = 0;

        for(int i = 0; i < texts.size(); i += this.batchSize) {
            List<String> batch = texts.subList(i, Math.min(i + this.batchSize, texts.size()));
            EmbeddingRequest request = EmbeddingRequest.builder().input(batch).model(this.modelName).build();
            EmbeddingResponse response = this.gigaChatClient.embeddings(request);

            chat.giga.model.embedding.Embedding embeddingItem;
            for(Iterator var8 = response.data().iterator(); var8.hasNext(); inputTokenCount += embeddingItem.usage().promptTokens()) {
                embeddingItem = (chat.giga.model.embedding.Embedding)var8.next();
                Embedding embedding = Embedding.from(embeddingItem.embedding());
                embeddings.add(embedding);
            }
        }

        return Response.from(embeddings, new TokenUsage(inputTokenCount));
    }

    @Generated
    public static GigaChatEmbeddingModelBuilder builder() {
        return new GigaChatEmbeddingModelBuilder();
    }

    @Generated
    public static class GigaChatEmbeddingModelBuilder {
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
        private String modelName;
        @Generated
        private Integer batchSize;

        @Generated
        GigaChatEmbeddingModelBuilder() {
        }

        @Generated
        public GigaChatEmbeddingModelBuilder apiHttpClient(HttpClient apiHttpClient) {
            this.apiHttpClient = apiHttpClient;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder authClient(AuthClient authClient) {
            this.authClient = authClient;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder readTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder connectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder logRequests(boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder logResponses(boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder verifySslCerts(boolean verifySslCerts) {
            this.verifySslCerts = verifySslCerts;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModelBuilder batchSize(Integer batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        @Generated
        public GigaChatEmbeddingModel build() {
            return new GigaChatEmbeddingModel(this.apiHttpClient, this.authClient, this.readTimeout, this.connectTimeout, this.apiUrl, this.logRequests, this.logResponses, this.verifySslCerts, this.modelName, this.batchSize);
        }

        @Generated
        public String toString() {
            String var10000 = String.valueOf(this.apiHttpClient);
            return "GigaChatEmbeddingModel.GigaChatEmbeddingModelBuilder(apiHttpClient=" + var10000 + ", authClient=" + String.valueOf(this.authClient) + ", readTimeout=" + this.readTimeout + ", connectTimeout=" + this.connectTimeout + ", apiUrl=" + this.apiUrl + ", logRequests=" + this.logRequests + ", logResponses=" + this.logResponses + ", verifySslCerts=" + this.verifySslCerts + ", modelName=" + this.modelName + ", batchSize=" + this.batchSize + ")";
        }
    }
}
