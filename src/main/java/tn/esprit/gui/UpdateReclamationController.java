package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entites.Reclamation;
import tn.esprit.services.ReclamationService;

public class UpdateReclamationController {
    @FXML private TextField commentaireField;

    private Reclamation reclamation;
    private ReclamationService reclamationService = new ReclamationService();

    // Method to set the reclamation object
    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    @FXML
    private void handleUpdate() {
        if (commentaireField.getText().isEmpty()) {
            System.err.println("Commentaire cannot be empty!");
            return;
        }

        // Update the reclamation object
        reclamation.setCommentaire(commentaireField.getText());
        reclamation.setStatut("traite");

        try {
            // Save the updated reclamation to the database
            reclamationService.updateReclamation(reclamation);
            System.out.println("Reclamation updated successfully!");

            // Close the pop-up window
            Stage stage = (Stage) commentaireField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}