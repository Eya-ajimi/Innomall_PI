package tn.esprit.gui;

import tn.esprit.entites.Reclamation;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tn.esprit.services.ReclamationService;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ClientReclamationController {
    @FXML private TextField descriptionField;
    @FXML private TextField nomshopField;

    private ReclamationService reclamationService = new ReclamationService();

    @FXML
    private void handleSubmit() {
        // Validate input fields
        if (descriptionField.getText().isEmpty() || nomshopField.getText().isEmpty()) {
            showAlert("Erreur de saisie", "Tous les champs doivent etre remplis.");
            return;
        }

        try {
            // Create a new Reclamation object
            Reclamation reclamation = new Reclamation(0, descriptionField.getText(), "", nomshopField.getText(), "non traite");

            // Add the reclamation to the database
            reclamationService.addReclamation(reclamation);

            // Clear the input fields
            descriptionField.clear();
            nomshopField.clear();

            // Show a success message
            showSuccessAlert("Reclamation envoyee", "Votre reclamation a ete envoyee avec succes.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s est produite lors de l envoi de la reclamation.");
        }
    }

    /**
     * Show an error alert with the given title and message.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show a success alert with the given title and message.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}