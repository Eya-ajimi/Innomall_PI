package tn.esprit.gui.oussemacontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.OussemaService.EventService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.util.List;

public class EventController {

    @FXML
    private FlowPane eventsContainer; // FlowPane to hold the event cards

    @FXML
    private ScrollPane scrollPane; // ScrollPane to make the FlowPane scrollable

    private EventService eventService = new EventService();
    private Session session = Session.getInstance(); // Session instance

    @FXML
    public void initialize() {
        // Bind the FlowPane's width to the ScrollPane's width
        eventsContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20)); // Subtract 20 for padding

        loadEvents();
    }

    /**
     * Load events from the database and display them in the FlowPane.
     */
    public void loadEvents() {
        eventsContainer.getChildren().clear(); // Clear existing cards

        // Fetch the current user from the session
        Utilisateur currentUser = session.getCurrentUser();
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        // Fetch events for the specific organizer
        List<Event> events = eventService.getEventsByOrganizer(currentUser.getId());

        for (Event event : events) {
            try {
                // Load the event card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event_card.fxml"));
                Parent eventCard = loader.load();

                // Set the event data in the card
                EventCardController controller = loader.getController();
                controller.setEvent(event);

                // Pass a reference to this MainController to the EventCardController
                controller.setMainController(this);

                // Add the card to the container
                eventsContainer.getChildren().add(eventCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddEvent() {
        try {
            // Load the event form FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event_form.fxml"));
            Parent root = loader.load();

            // Get the controller for the form
            EventFormController formController = loader.getController();

            // Pass null to indicate this is a new event
            formController.setEvent(null);

            // Fetch the current user from the session
            Utilisateur currentUser = session.getCurrentUser();
            if (currentUser != null) {
                formController.setIdOrganisateur(currentUser.getId());
            }

            // Open the form in a new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Event");
            stage.showAndWait();

            // Refresh the event list after adding a new event
            if (formController.isUpdated()) {
                // Add the new event to the database
                Event newEvent = formController.getEvent();
                eventService.add(newEvent);

                // Refresh the event list
                loadEvents();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}