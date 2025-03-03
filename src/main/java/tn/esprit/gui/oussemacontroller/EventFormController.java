package tn.esprit.gui.oussemacontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.OussemaService.EventService;
import tn.esprit.utils.Session;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventFormController {

    @FXML
    private TextField eventNameField;

    @FXML
    private TextField eventDescriptionField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField locationField;

    private Event event; // This will hold the event being added or modified
    private boolean isNewEvent = false; // Flag to check if this is a new event
    private boolean updated = false; // Flag to check if the event was updated
    private EventService eventService = new EventService();
    private int idOrganisateur; // Add this field

    private Session session = Session.getInstance(); // Session instance

    // Setter for idOrganisateur
    public void setIdOrganisateur(int idOrganisateur) {
        this.idOrganisateur = idOrganisateur;
    }

    public void setEvent(Event event) {

        if (event == null) {
            // This is a new event
            this.event = new Event(); // Initialize a new event
            this.isNewEvent = true; // Set the flag to indicate this is a new event
        } else {
            // This is an existing event being modified
            this.event = event;
            this.isNewEvent = false; // Set the flag to indicate this is an existing event

            // Pre-fill the form with the event's current data
            eventNameField.setText(event.getNomOrganisateur()); // Set the organizer name
            eventDescriptionField.setText(event.getDescription()); // Set the description
            locationField.setText(event.getEmplacement()); // Set the location

            // Convert String dates to LocalDate for DatePicker
            if (event.getDateDebut() != null && !event.getDateDebut().isEmpty()) {
                startDatePicker.setValue(LocalDate.parse(event.getDateDebut(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }
            if (event.getDateFin() != null && !event.getDateFin().isEmpty()) {
                endDatePicker.setValue(LocalDate.parse(event.getDateFin(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }
        }
    }

    public boolean isUpdated() {
        return updated;
    }


    public Event getEvent() {
        return event;
    }

    @FXML
    private void handleConfirm() {
        // Validate input fields
        if (eventNameField.getText().isEmpty() ||
                eventDescriptionField.getText().isEmpty() ||
                startDatePicker.getValue() == null ||
                endDatePicker.getValue() == null ||
                locationField.getText().isEmpty()) {
            // Show an error message if any field is empty
            showErrorAlert("Missing Information", "Please fill in all fields.");
            return;
        }

        // Validate that the end date is after the start date
        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            showErrorAlert("Invalid Date Range", "The end date must be after the start date.");
            return;
        }

        // Ensure the event object is not null
        if (this.event == null) {
            System.out.println("Event is null. Initializing a new event.");
            this.event = new Event(); // Initialize a new event
        }

        // Update the event object with the new data
        event.setNomOrganisateur(eventNameField.getText());
        event.setDescription(eventDescriptionField.getText());
        event.setDateDebut(startDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        event.setDateFin(endDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        event.setEmplacement(locationField.getText());

        // Fetch the current user from the session
        Utilisateur currentUser = session.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Current user is null. Cannot set organizer ID.");
            showErrorAlert("Error", "No user is logged in.");
            return; // Exit the method if the user is not logged in
        }
        System.out.println("Current user ID: " + currentUser.getId());

        // Set the organizer ID
        event.setIdOrganisateur(currentUser.getId());

        // Debugging: Print the event object
        System.out.println("Event before saving: " + event.toString());

        // Mark the event as updated
        updated = true;

        // Close the form
        Stage stage = (Stage) eventNameField.getScene().getWindow();
        stage.close();
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}