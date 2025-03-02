package tn.esprit.gui.eyacontroller;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenAIClient {
    private static final String API_KEY = "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"; // Replace with your OpenAI API key
    private static final String API_URL = "https://api.openai.com/v1/chat/completions"; // OpenAI GPT API endpoint

    public String getChatResponse(String userMessage) throws IOException {
        // Create the request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo"); // Use GPT-3.5 model
        requestBody.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                .put(new JSONObject().put("role", "user").put("content", userMessage))
        );
        requestBody.put("max_tokens", 150); // Limit response length

        // Debug: Print the request body
        System.out.println("Request Body: " + requestBody.toString());

        // Create the HTTP connection
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setDoOutput(true);

        // Send the request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Get the response
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            // Debug: Print the error response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = br.readLine()) != null) {
                    errorResponse.append(errorLine.trim());
                }
                System.out.println("Error Response: " + errorResponse.toString());
            }
            throw new IOException("Unexpected code: " + responseCode);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Debug: Print the raw API response
            System.out.println("Raw API Response: " + response.toString());

            // Parse the response
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content"); // Extract the generated text
        } finally {
            connection.disconnect();
        }
    }
}
