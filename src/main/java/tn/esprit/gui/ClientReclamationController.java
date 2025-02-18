package tn.esprit.gui;

import tn.esprit.entites.Reclamation;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tn.esprit.services.ReclamationService;

public class ClientReclamationController {
    @FXML private TextField descriptionField;
    @FXML private TextField nomshopField;

    private ReclamationService reclamationService = new ReclamationService();

    @FXML
    private void handleSubmit() {
        try {
            Reclamation reclamation = new Reclamation(0, descriptionField.getText(), "", nomshopField.getText(), "non traite");
            reclamationService.addReclamation(reclamation);
            descriptionField.clear();
            nomshopField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}