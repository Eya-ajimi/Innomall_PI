package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane; // Updated import
import tn.esprit.entites.Event;
import tn.esprit.services.EventService;

import java.io.IOException;
import java.util.List;

public class ClientEventViewController {

    @FXML
    private FlowPane eventsContainer; // Updated from VBox to FlowPane

    private EventService eventService = new EventService();

    @FXML
    public void initialize() {
        loadEvents();
    }

    private void loadEvents() {
        eventsContainer.getChildren().clear(); // Clear existing cards
        List<Event> events = eventService.getAll();

        for (Event event : events) {
            try {
                // Load the client event card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client_event_card.fxml"));
                Parent eventCard = loader.load();

                // Set the event data in the card
                ClientEventCardController controller = loader.getController();
                controller.setEvent(event);

                // Add the card to the container
                eventsContainer.getChildren().add(eventCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}