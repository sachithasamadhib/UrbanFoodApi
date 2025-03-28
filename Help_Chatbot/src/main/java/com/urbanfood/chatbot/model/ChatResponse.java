package com.urbanfood.chatbot.model;

public class ChatResponse {
    private String answer;

    // Default constructor
    public ChatResponse() {
    }

    public ChatResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

