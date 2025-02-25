package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;
import tn.esprit.entites.Event;
import tn.esprit.services.EventService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ClientEventViewController {

    @FXML
    private FlowPane eventsContainer;

    @FXML
    private DatePicker searchDatePicker;

    @FXML
    private EventService eventService = new EventService();

    @FXML
    public void initialize() {
        loadEvents(null); // Load all events initially
    }

    @FXML
    private void handleSearch() {
        LocalDate selectedDate = searchDatePicker.getValue();
        loadEvents(selectedDate);
    }

    private void loadEvents(LocalDate date) {
        eventsContainer.getChildren().clear(); // Clear existing cards
        List<Event> events = date == null ? eventService.getAll() : eventService.getEventsByDate(date);

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