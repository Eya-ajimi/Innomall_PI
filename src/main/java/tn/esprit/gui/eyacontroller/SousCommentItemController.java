package tn.esprit.gui.eyacontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent; // Add this import
import tn.esprit.entities.SousCommentaire;
import tn.esprit.entities.Utilisateur;
import tn.esprit.utils.Session;


import javafx.scene.control.*;
import tn.esprit.services.eyaservice.SousCommentaireService;

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

        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser != null) {
            userIdLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom()); // Display user's full name
        } else {
            userIdLabel.setText("Utilisateur " + sousCommentaire.getUtilisateurId()); // Fallback to user ID
        }
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
        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser == null || currentUser.getId() != sousCommentaire.getUtilisateurId()) {
            showAlert("Erreur", "Le sous-commentaire contient des mots inappropriés.");
            return;
        }

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
        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser == null || currentUser.getId() != sousCommentaire.getUtilisateurId()) {
            showAlert("Erreur", "Vous ne pouvez supprimer que vos propres sous-commentaires.");
            return;
        }

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
        Alert alert = new Alert(Alert.AlertType.ERROR); // Default to ERROR type
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}