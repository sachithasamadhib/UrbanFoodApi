package com.urbanfood.chatbot.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    private final Map<String, String> knowledgeBase;

    public ChatbotService() {
        knowledgeBase = new HashMap<>();
        initializeKnowledgeBase();
    }

    private void initializeKnowledgeBase() {
        // Add key phrases and responses related to the urban food website
        knowledgeBase.put("hours", 
            "Our restaurant is open from 10 AM to 10 PM, Monday through Sunday.");
        
        knowledgeBase.put("menu", 
            "We offer a variety of urban cuisine including burgers, salads, fusion dishes, and vegan options.");
        
        knowledgeBase.put("location", 
            "We are located at 123 Urban Street, Downtown.");
        
        knowledgeBase.put("delivery", 
            "Yes, we offer both delivery and takeout options.");
        
        knowledgeBase.put("reservation", 
            "Reservations can be made through our website or by calling us at (555) 123-4567.");
        
        knowledgeBase.put("special", 
            "We have daily specials and a happy hour from 4 PM to 6 PM.");
        
        knowledgeBase.put("vegan", 
            "We offer various vegan, vegetarian, and gluten-free options.");
        
        knowledgeBase.put("contact", 
            "You can contact us at info@urbanfood.com or call us at (555) 123-4567.");
    }

    public String processQuery(String query) {
        // Convert query to lowercase for easier matching
        String lowerQuery = query.toLowerCase();
        
        // Check if query is not about the website
        if (lowerQuery.contains("weather") || lowerQuery.contains("news") || 
            lowerQuery.contains("politics") || lowerQuery.contains("sports")) {
            return "I can only answer questions regarding the Urban Food website and restaurant.";
        }

        // Look for matching keywords in the query
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lowerQuery.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Default response if no keyword matches
        return "I don't have information about that. Please ask about our hours, menu, location, delivery, reservations, specials, dietary options, or contact information.";
    }
}

