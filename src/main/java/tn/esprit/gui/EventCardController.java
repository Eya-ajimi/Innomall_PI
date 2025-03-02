package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.entites.Event;
import tn.esprit.services.EventService;

import java.io.IOException;

public class EventCardController {

    @FXML
    private Label eventName;

    @FXML
    private Label eventDescription;

    @FXML
    private Label eventDates;

    @FXML
    private Label eventLocation;

    private Event event;
    private EventService eventService = new EventService();
    private EventController mainController; // Reference to the MainController

    public void setEvent(Event event) {
        this.event = event;
        eventName.setText(event.getNomOrganisateur());
        eventDescription.setText(event.getDescription());
        eventDates.setText(event.getDateDebut() + " - " + event.getDateFin());
        eventLocation.setText(event.getEmplacement());
    }

    // Setter for MainController
    public void setMainController(EventController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleModifyEvent() {
        try {
            // Load the event form FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event_form.fxml"));
            Parent root = loader.load();

            // Get the controller for the form
            EventFormController formController = loader.getController();

            // Pre-fill the form with the current event data
            formController.setEvent(event);

            // Open the form in a new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modify Event");
            stage.showAndWait();

            // Refresh the event list after modification
            if (formController.isUpdated()) {
                // Update the event in the database
                eventService.update(event);

                // Refresh the event card display
                setEvent(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteEvent() {
        // Delete the event from the database
        eventService.delete(event.getId());

        // Notify the MainController to refresh the event list
        if (mainController != null) {
            mainController.loadEvents();
        }
    }
}