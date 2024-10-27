package com.aidiary.domain.emotion.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChatGPTReq {
    private String model;
    private List<ChatGPTMessage> messages;

    public ChatGPTReq(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new ChatGPTMessage("user", prompt));
    }
}
