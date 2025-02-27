package tn.esprit.services;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1beta1.*;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1beta1.*;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeminiService {

    private static final String PROJECT_ID = "innomallai";
    private static final String LOCATION = "us-central1";
    private static final String MODEL_ID = "gemini-pro";
    private static final String CREDENTIALS_PATH = "C:\\Users\\ajimi\\Documents\\innomallai-bdffe95c47c8.json"; // Path to your service account key file
    private static final String ENDPOINT = "us-central1-aiplatform.googleapis.com:443";

    public String getResponse(String prompt) {
        try {
            // Load credentials from the key file
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_PATH));

            // Create the prediction service client with credentials
            PredictionServiceSettings settings = PredictionServiceSettings.newBuilder()
                    .setEndpoint(ENDPOINT)
                    .setCredentialsProvider(() -> credentials)
                    .build();

            try (PredictionServiceClient client = PredictionServiceClient.create(settings)) {
                // Prepare the input
                Map<String, Object> inputMap = new HashMap<>();
                inputMap.put("prompt", prompt);

                // Convert the input map to a Value object
                Value.Builder inputValueBuilder = Value.newBuilder();
                JsonFormat.parser().merge(JsonFormat.printer().print(inputValueBuilder.build()), inputValueBuilder);

                // Create the PredictRequest
                PredictRequest predictRequest = PredictRequest.newBuilder()
                        .setEndpoint(String.format("projects/%s/locations/%s/publishers/google/models/%s", PROJECT_ID, LOCATION, MODEL_ID))
                        .addInstances(inputValueBuilder.build())
                        .build();

                // Make the prediction request
                PredictResponse response = client.predict(predictRequest);

                // Parse the response
                if (response.getPredictionsCount() > 0) {
                    Value prediction = response.getPredictions(0);
                    return prediction.getStringValue(); // Extract the response text
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Sorry, I couldn't process your request.";
    }
}