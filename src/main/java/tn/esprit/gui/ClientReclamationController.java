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
        if (descriptionField.getText().isEmpty() || nomshopField.getText().isEmpty()) {
            showAlert("Erreur de saisie", "Tous les champs doivent etre remplis.");
            return;
        }

        try {
            Reclamation reclamation = new Reclamation(0, descriptionField.getText(), "", nomshopField.getText(), "non traite");
            reclamationService.addReclamation(reclamation);
            descriptionField.clear();
            nomshopField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}