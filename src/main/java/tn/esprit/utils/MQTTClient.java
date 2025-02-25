package tn.esprit.utils;

import org.eclipse.paho.client.mqttv3.*;

public class MQTTClient {
    private static final String BROKER_URL = "tcp://192.168.254.112:1883"; // Use your local IP
    private static final String CLIENT_ID = "JavaFXClient";
    private static final String TOPIC = "placeparking/status"; // ESP32 publishes here

    private MqttClient client;
    private MQTTMessageHandler messageHandler;

    public MQTTClient(MQTTMessageHandler handler) {
        this.messageHandler = handler;
        try {
            client = new MqttClient(BROKER_URL, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);
            subscribeToTopic();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToTopic() {
        try {
            client.subscribe(TOPIC, (topic, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Received: " + payload);
                messageHandler.handleMessage(topic, payload); // Pass the payload to the handler
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}