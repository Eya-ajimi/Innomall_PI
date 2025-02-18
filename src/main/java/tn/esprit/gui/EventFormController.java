package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entites.Event;
import tn.esprit.services.EventService;

public class EventFormController {

    @FXML
    private TextField eventNameField;

    @FXML
    private TextField eventDescriptionField;

    @FXML
    private TextField startDateField;

    @FXML
    private TextField endDateField;

    @FXML
    private TextField locationField;

    private Event event; // This will hold the event being added or modified
    private boolean isNewEvent = false; // Flag to check if this is a new event
    private boolean updated = false; // Flag to check if the event was updated
    private EventService eventService = new EventService();

    /**
     * Set the event to be modified or initialize a new event.
     *
     * @param event The event to modify, or null to add a new event.
     */
    public void setEvent(Event event) {
        if (event == null) {
            // This is a new event
            this.event = new Event();
            this.isNewEvent = true;
        } else {
            // This is an existing event being modified
            this.event = event;
            this.isNewEvent = false;

            // Pre-fill the form with the event's current data
            eventNameField.setText(event.getNomOrganisateur());
            eventDescriptionField.setText(event.getDescription());
            startDateField.setText(event.getDateDebut());
            endDateField.setText(event.getDateFin());
            locationField.setText(event.getEmplacement());
        }
    }

    /**
     * Check if the event was updated or added.
     *
     * @return True if the event was updated or added, false otherwise.
     */
    public boolean isUpdated() {
        return updated;
    }

    /**
     * Get the event (used after the form is closed).
     *
     * @return The event that was added or modified.
     */
    public Event getEvent() {
        return event;
    }

    @FXML
    private void handleConfirm() {
        // Validate input fields
        if (eventNameField.getText().isEmpty() ||
                eventDescriptionField.getText().isEmpty() ||
                startDateField.getText().isEmpty() ||
                endDateField.getText().isEmpty() ||
                locationField.getText().isEmpty()) {
            // Show an error message if any field is empty
            showErrorAlert("Missing Information", "Please fill in all fields.");
            return; // Exit the method if validation fails
        }

        // Validate date format for start date
        if (!isValidDate(startDateField.getText())) {
            showErrorAlert("Invalid Start Date", "The start date must be in the format jj-mm-aaaa.");
            return; // Exit the method if validation fails
        }

        // Validate date format for end date
        if (!isValidDate(endDateField.getText())) {
            showErrorAlert("Invalid End Date", "The end date must be in the format jj-mm-aaaa.");
            return; // Exit the method if validation fails
        }

        // Update the event object with the new data
        event.setNomOrganisateur(eventNameField.getText());
        event.setDescription(eventDescriptionField.getText());
        event.setDateDebut(startDateField.getText());
        event.setDateFin(endDateField.getText());
        event.setEmplacement(locationField.getText());

        // Set the idOrganisateur (you need to get this value from somewhere)
        // For example, you can hardcode it for testing, or get it from the logged-in user
        int idOrganisateur = 1; // Replace this with the actual idOrganisateur
        event.setIdOrganisateur(idOrganisateur);

        // Mark the event as updated
        updated = true;

        // Close the form
        Stage stage = (Stage) eventNameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Validate if the date is in the format jj-mm-aaaa.
     *
     * @param date The date to validate.
     * @return True if the date is valid, false otherwise.
     */
    private boolean isValidDate(String date) {
        // Regex to match the format jj-mm-aaaa
        String regex = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})$";
        return date.matches(regex);
    }

    /**
     * Show an error alert with the given title and message.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}