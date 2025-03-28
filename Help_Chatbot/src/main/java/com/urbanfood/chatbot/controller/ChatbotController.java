package com.urbanfood.chatbot.controller;

import com.urbanfood.chatbot.model.ChatRequest;
import com.urbanfood.chatbot.model.ChatResponse;
import com.urbanfood.chatbot.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public ChatResponse askQuestion(@RequestBody ChatRequest request) {
        String answer = chatbotService.processQuery(request.getQuery());
        return new ChatResponse(answer);
    }
}

