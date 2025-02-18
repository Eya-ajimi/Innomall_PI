package tn.esprit.gui;

import tn.esprit.entites.Reclamation;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import tn.esprit.services.ReclamationService;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AdminReclamationController {
    @FXML private ListView<Reclamation> reclamationList;
    @FXML private TextField commentaireField;

    private ReclamationService reclamationService = new ReclamationService();

    @FXML
    private void initialize() {
        // Set up the custom cell factory
        reclamationList.setCellFactory(new Callback<ListView<Reclamation>, ListCell<Reclamation>>() {
            @Override
            public ListCell<Reclamation> call(ListView<Reclamation> param) {
                return new ListCell<Reclamation>() {
                    @Override
                    protected void updateItem(Reclamation reclamation, boolean empty) {
                        super.updateItem(reclamation, empty);
                        if (empty || reclamation == null) {
                            setText(null);
                        } else {
                            // Customize the display text to exclude the ID
                            setText(reclamation.getDescription());
                        }
                    }
                };
            }
        });

        try {
            reclamationList.getItems().addAll(reclamationService.getAllReclamations());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        Reclamation selectedReclamation = reclamationList.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            if (commentaireField.getText().isEmpty()) {
                showAlert("Erreur de saisie", "Le champ commentaire ne peut pas etre vide.");
                return;
            }

            try {
                selectedReclamation.setCommentaire(commentaireField.getText());
                selectedReclamation.setStatut("traite");
                reclamationService.updateReclamation(selectedReclamation);
                reclamationList.refresh();
                commentaireField.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez selectionner une reclamation a mettre a jour.");
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