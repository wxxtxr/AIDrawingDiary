package com.aidiary.domain.emotion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatGPTReq2 {
    private String model;
    private Messages[] messages;

    public ChatGPTReq2(String model, String systemMessage, String userMessage) {
        this.model = model;
        this.messages = new Messages[] {
                new Messages("system", systemMessage),
                new Messages("user", userMessage)
        };
    }

    public String getModel() {
        return model;
    }

    public Messages[] getMessages() {
        return messages;
    }

    public static class Messages {
        private String role;
        private String content;

        public Messages(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
