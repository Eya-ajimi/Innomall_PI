#include <WiFi.h>
#include <PubSubClient.h>

// WiFi credentials
const char* ssid = "ORANGE_3298";   // Replace with your WiFi SSID
const char* password = "3E8L74i8";  // Replace with your WiFi password

// MQTT broker details
const char* mqtt_server = "192.168.1.195";       // Replace with your MQTT broker IP
const int mqtt_port = 1883;                      // Default MQTT port
const char* mqtt_topic = "placeparking/status";  // MQTT topic

// Parking spot configuration
const int numSpots = 3;                     // Number of parking spots
const int parkingSpots[] = { 21, 22, 23 };  // IDs of the parking spots
const int inputPins[] = { 21, 22, 23 };        // GPIO pins for parking spot inputs (e.g., buttons or sensors)

// WiFi and MQTT clients
WiFiClient espClient;
PubSubClient client(espClient);

// Function to connect to WiFi
void setupWiFi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to WiFi: ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

// Function to handle incoming MQTT messages
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message received on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

// Function to reconnect to MQTT broker
void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (client.connect("ESP32Client")) {
      Serial.println("connected");
      client.subscribe(mqtt_topic);  // Subscribe to the topic
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

// Function to check parking spot status and publish to MQTT
void checkParkingSpots() {
  for (int i = 0; i < numSpots; i++) {
    int spotId = parkingSpots[i];
    int inputPin = inputPins[i];

    // Read the input state (HIGH = taken, LOW = free)
    bool isTaken = digitalRead(inputPin) == HIGH;

    // Prepare the MQTT message
    String message = String(spotId) + ":" + (isTaken ? "taken" : "free");

    // Publish the status to the MQTT topic
    client.publish(mqtt_topic, message.c_str());
    Serial.println("Published: " + message);

    delay(500);  // Small delay between checking each spot
  }
}

void setup() {
  Serial.begin(115200);

  // Set up input pins
  for (int i = 0; i < numSpots; i++) {
    pinMode(inputPins[i], INPUT);
  }

  // Connect to WiFi
  setupWiFi();

  // Configure MQTT server and callback
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
}

void loop() {
  // Reconnect to MQTT broker if disconnected
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // Check parking spot status and publish to MQTT
  checkParkingSpots();

  delay(2000);  // Wait 2 seconds before checking again
}