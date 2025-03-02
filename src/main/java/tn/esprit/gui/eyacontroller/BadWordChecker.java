package tn.esprit.gui.eyacontroller;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BadWordChecker {

    private static final String API_URL = "https://www.purgomalum.com/service/containsprofanity";

    public static boolean containsBadWords(String text) {
        try {
            // Encode the text for the URL
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String fullUrl = API_URL + "?text=" + encodedText;

            // Create HTTP client and request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the response (true if bad words are found, false otherwise)
            return Boolean.parseBoolean(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Assume no bad words if there's an error
        }
    }
}
