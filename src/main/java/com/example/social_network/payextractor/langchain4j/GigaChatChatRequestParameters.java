//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.social_network.payextractor.langchain4j;

import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.DefaultChatRequestParameters;
import java.util.List;
import lombok.Generated;

public class GigaChatChatRequestParameters extends DefaultChatRequestParameters {
    private final Integer updateInterval;
    private final Boolean stream;
    private final Boolean profanityCheck;
    private final Object functionCall;
    private final List<String> attachments;
    private final Float repetitionPenalty;

    private GigaChatChatRequestParameters(GigaChatBuilder builder) {
        super(builder);
        this.updateInterval = builder.updateInterval;
        this.stream = builder.stream;
        this.profanityCheck = builder.profanityCheck;
        this.functionCall = builder.functionCall;
        this.attachments = builder.attachments;
        this.repetitionPenalty = builder.repetitionPenalty;
    }

    public GigaChatChatRequestParameters overrideWith(ChatRequestParameters that) {
        return builder().overrideWith(this).overrideWith(that).build();
    }

    public static GigaChatBuilder builder() {
        return new GigaChatBuilder();
    }

    @Generated
    public Integer getUpdateInterval() {
        return this.updateInterval;
    }

    @Generated
    public Boolean getStream() {
        return this.stream;
    }

    @Generated
    public Boolean getProfanityCheck() {
        return this.profanityCheck;
    }

    @Generated
    public Object getFunctionCall() {
        return this.functionCall;
    }

    @Generated
    public List<String> getAttachments() {
        return this.attachments;
    }

    @Generated
    public Float getRepetitionPenalty() {
        return this.repetitionPenalty;
    }

    public static class GigaChatBuilder extends DefaultChatRequestParameters.Builder<GigaChatBuilder> {
        private Integer updateInterval;
        private Boolean stream;
        private Boolean profanityCheck;
        private Object functionCall;
        private List<String> attachments;
        private Float repetitionPenalty;

        public GigaChatBuilder() {
        }

        public GigaChatBuilder updateInterval(Integer updateInterval) {
            this.updateInterval = updateInterval;
            return this;
        }

        public GigaChatBuilder profanityCheck(Boolean profanityCheck) {
            this.profanityCheck = profanityCheck;
            return this;
        }

        public GigaChatBuilder functionCall(Object functionCall) {
            this.functionCall = functionCall;
            return this;
        }

        public GigaChatBuilder attachments(List<String> attachments) {
            this.attachments = attachments;
            return this;
        }

        public GigaChatBuilder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }

        public GigaChatBuilder repetitionPenalty(Float repetitionPenalty) {
            this.repetitionPenalty = repetitionPenalty;
            return this;
        }

        public GigaChatChatRequestParameters build() {
            return new GigaChatChatRequestParameters(this);
        }

        public GigaChatBuilder overrideWith(ChatRequestParameters parameters) {
            super.overrideWith(parameters);
            if (parameters instanceof GigaChatChatRequestParameters chatChatRequestParameters) {
                this.updateInterval((Integer)Utils.getOrDefault(chatChatRequestParameters.getUpdateInterval(), this.updateInterval));
                this.profanityCheck((Boolean)Utils.getOrDefault(chatChatRequestParameters.getProfanityCheck(), this.profanityCheck));
                this.functionCall(Utils.getOrDefault(chatChatRequestParameters.getFunctionCall(), this.functionCall));
                this.attachments(Utils.getOrDefault(chatChatRequestParameters.getAttachments(), this.attachments));
                this.stream((Boolean)Utils.getOrDefault(chatChatRequestParameters.getStream(), this.stream));
                this.repetitionPenalty((Float)Utils.getOrDefault(chatChatRequestParameters.getRepetitionPenalty(), this.repetitionPenalty));
            }

            return this;
        }
    }
}
