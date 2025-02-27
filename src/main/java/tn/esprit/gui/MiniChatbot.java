package tn.esprit.gui;

import java.util.HashMap;
import java.util.Map;

public class MiniChatbot {
    private final Map<String, String> responses;

    public MiniChatbot() {
        // Initialize the predefined questions and responses
        responses = new HashMap<>();
        responses.put("hello", "Hello! How can I help you?");
        responses.put("how many points do i need to earn product", "You need 1000 points to earn a product.");
        responses.put("what is the deadline for earning a product", "No deadline is set.");
        responses.put("how do i check my points", "You can check your points in the app.");
        responses.put("what products can i earn", "You can earn various products such as electronics, gift cards, and more.");
        responses.put("how do i earn points", "You can earn points by completing tasks, making purchases, or referring friends.");
        responses.put("can i transfer my points", "No, points are non-transferable.");
        responses.put("thank you", "at anytime .. :)");
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

        // Handle range-based answers
        if (lowercaseMessage.contains("how many points do i have")) {
            return handlePointsRangeResponse(lowercaseMessage);
        }

        // Default response if no match is found
        return "I'm sorry, I don't understand that question. Can you please rephrase?";
    }

    private String handlePointsRangeResponse(String message) {
        // Extract the number of points from the message
        int points = extractPointsFromMessage(message);

        if (points >= 0) {
            if (points < 500) {
                return "You have " + points + " points. You need " + (1000 - points) + " more points to earn a product.";
            } else if (points < 1000) {
                return "You have " + points + " points. You're close! You need " + (1000 - points) + " more points to earn a product.";
            } else {
                return "You have " + points + " points. Congratulations! You can now earn a product.";
            }
        } else {
            return "I couldn't determine how many points you have. Please specify the number of points.";
        }
    }

    private int extractPointsFromMessage(String message) {
        // Simple extraction of numbers from the message
        String[] words = message.split(" ");
        for (String word : words) {
            try {
                return Integer.parseInt(word);
            } catch (NumberFormatException e) {
                // Ignore and continue
            }
        }
        return -1; // Return -1 if no number is found
    }
}