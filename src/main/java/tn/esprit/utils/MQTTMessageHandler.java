package tn.esprit.utils;

import tn.esprit.services.sofieneservice.PlaceParkingService;
import tn.esprit.entities.enums.StatutPlace;

import java.sql.SQLException;

public class MQTTMessageHandler {
    private PlaceParkingService placeParkingService;

    public MQTTMessageHandler(PlaceParkingService service) {
        this.placeParkingService = service;
    }

    private int extractParkingId(String payload) {
        try {
            // Assuming the format is "id:status" (e.g., "1:free")
            String[] parts = payload.split(":");
            return Integer.parseInt(parts[0]); // Convert first part to integer
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid MQTT message format: " + payload);
            return -1; // Return -1 for error handling
        }
    }

    private StatutPlace extractStatus(String payload) {
        try {
            // Assuming the format is "id:status" (e.g., "1:free")
            String[] parts = payload.split(":");
            return StatutPlace.valueOf(parts[1].toLowerCase()); // Convert second part to StatutPlace enum
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid status in MQTT message: " + payload);
            return null; // Return null for error handling
        }
    }

    public void handleMessage(String topic, String payload) {
        System.out.println("Received MQTT message: " + payload);

        int parkingId = extractParkingId(payload); // Extract ID from message
        StatutPlace status = extractStatus(payload); // Extract status from message

        if (parkingId != -1 && status != null) {
            try {
                placeParkingService.updateParkingSpotStatus(parkingId, status); // Update the database
                System.out.println("Updated parking status for ID: " + parkingId + " to " + status);
            } catch (SQLException e) {
                System.err.println("Error updating parking status: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Invalid MQTT message format or status: " + payload);
        }
    }
}