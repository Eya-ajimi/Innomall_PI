package tn.esprit.gui.sofienecontroller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.ParkingAssignment;
import tn.esprit.services.sofieneservice.ParkingAssignmentService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class FindMyCarController {
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Label resultLabel;
    @FXML
    private Button findButton;
    @FXML
    private Button cancelButton;

    // New UI elements
    @FXML
    private VBox resultContainer;
    @FXML
    private VBox carFoundContainer;
    @FXML
    private Label spotNumberLabel;
    @FXML
    private Label scannedAtLabel;
    @FXML
    private Label parkedForLabel;

    private final ParkingAssignmentService assignmentService = new ParkingAssignmentService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    @FXML
    private void initialize() {
        // Setup phone number validation listener
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneNumberField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 8) {
                phoneNumberField.setText(oldValue);
            }
        });
    }

    @FXML
    private void handleFindAction() {
        String phoneNumber = phoneNumberField.getText().trim();

        if (phoneNumber.isEmpty() || !phoneNumber.matches("\\d{8}")) {
            showErrorMessage("Please enter a valid 8-digit phone number");
            return;
        }

        try {
            ParkingAssignment assignment = assignmentService.findLatestAssignment(phoneNumber);
            if (assignment != null) {
                showCarFound(assignment);
            } else {
                showErrorMessage("No parking record found for this phone number");
            }
        } catch (SQLException e) {
            showErrorMessage("Error searching for your car: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showCarFound(ParkingAssignment assignment) {
        // Set visibility
        resultContainer.setVisible(true);
        resultContainer.setManaged(true);
        carFoundContainer.setVisible(true);
        carFoundContainer.setManaged(true);
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);

        // Update UI elements
        spotNumberLabel.setText("Spot #" + assignment.getPlaceParkingId());
        scannedAtLabel.setText(dateFormat.format(assignment.getScannedAt()));
        parkedForLabel.setText(assignmentService.getParkingDuration(assignment));
    }

    private void showErrorMessage(String message) {
        // Set visibility
        resultContainer.setVisible(true);
        resultContainer.setManaged(true);
        carFoundContainer.setVisible(false);
        carFoundContainer.setManaged(false);
        resultLabel.setVisible(true);
        resultLabel.setManaged(true);

        // Update error message
        resultLabel.setText(message);
        resultLabel.getStyleClass().removeAll("success-text", "info-text");
        resultLabel.getStyleClass().add("error-text");
    }

    @FXML
    private void handleCancelAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}