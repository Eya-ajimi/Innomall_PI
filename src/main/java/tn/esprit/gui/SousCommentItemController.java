package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent; // Add this import
import tn.esprit.entites.SousCommentaire;



import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import tn.esprit.entites.SousCommentaire;
import tn.esprit.services.SousCommentaireService;

import java.util.Optional;

public class SousCommentItemController {

    @FXML
    private Label userIdLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Button btnEditDelete;

    private SousCommentaire sousCommentaire;
    private final SousCommentaireService sousCommentaireService = new SousCommentaireService();

    public void setSousCommentaire(SousCommentaire sousCommentaire) {
        this.sousCommentaire = sousCommentaire;
        userIdLabel.setText("Utilisateur " + sousCommentaire.getUtilisateurId());
        contentLabel.setText(sousCommentaire.getContenu());
        timeLabel.setText(sousCommentaire.getDateCreation());
    }

    @FXML
    private void initialize() {
        // Add event handler for the edit/delete button
        btnEditDelete.setOnMouseClicked(event -> showContextMenu(event));
    }

    private void showContextMenu(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editMenuItem = new MenuItem("Modifier");
        editMenuItem.setOnAction(e -> editSousCommentaire());

        MenuItem deleteMenuItem = new MenuItem("Supprimer");
        deleteMenuItem.setOnAction(e -> deleteSousCommentaire());

        contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
        contextMenu.show(btnEditDelete, event.getScreenX(), event.getScreenY());
    }

    private void editSousCommentaire() {
        TextInputDialog dialog = new TextInputDialog(sousCommentaire.getContenu());
        dialog.setTitle("Modifier le sous-commentaire");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez votre nouveau sous-commentaire :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newContent -> {
            if (!newContent.trim().isEmpty()) {
                sousCommentaire.setContenu(newContent);
                try {
                    sousCommentaireService.update(sousCommentaire);
                    contentLabel.setText(newContent); // Update the displayed content
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la mise à jour du sous-commentaire : " + e.getMessage());
                }
            }
        });
    }

    private void deleteSousCommentaire() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer ce sous-commentaire ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                sousCommentaireService.delete(sousCommentaire);
                // Remove this item from the UI (you may need to notify the parent controller)
                btnEditDelete.getParent().setVisible(false); // Hide the HBox containing this sous-commentaire
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression du sous-commentaire : " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}