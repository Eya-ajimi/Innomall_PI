package tn.esprit.tests;

import tn.esprit.utils.MQTTClient;
import tn.esprit.utils.MQTTMessageHandler;
import tn.esprit.services.PlaceParkingService;

public class MQTTTest {
    public static void main(String[] args) {
        // Initialize the PlaceParkingService
        PlaceParkingService placeParkingService = new PlaceParkingService();

        // Initialize the MQTTMessageHandler with the service
        MQTTMessageHandler messageHandler = new MQTTMessageHandler(placeParkingService);

        // Start the MQTT client
        MQTTClient mqttClient = new MQTTClient(messageHandler);

        // Keep the application running to listen for MQTT messages
        System.out.println("MQTT Client started. Waiting for messages...");

        // Add a loop to keep the application running
        while (true) {
            try {
                Thread.sleep(1000); // Sleep to avoid high CPU usage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}