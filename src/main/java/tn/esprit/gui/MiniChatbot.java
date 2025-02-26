package tn.esprit.gui;



import java.util.HashMap;
import java.util.Map;

public class MiniChatbot {
    private final Map<String, String> responses;

    public MiniChatbot() {
        // Initialize the predefined questions and responses
        responses = new HashMap<>();
        responses.put("hello", "Hello! How can I help you?");
        responses.put("how many points do i need to earn x", "You need 1000 points to earn X.");
        responses.put("what is the deadline for earning x", "The deadline is December 31, 2023.");
        responses.put("how do i check my points", "You can check your points in the app.");
    }

    public String getResponse(String userMessage) {
        // Convert the user's message to lowercase for case-insensitive matching
        String lowercaseMessage = userMessage.toLowerCase();

        // Check if the message matches any predefined question
        for (Map.Entry<String, String> entry : responses.entrySet()) {
            if (lowercaseMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Default response if no match is found
        return "I'm sorry, I don't understand that question. Can you please rephrase?";
    }
}