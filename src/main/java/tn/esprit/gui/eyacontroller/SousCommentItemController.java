package tn.esprit.gui.eyacontroller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import tn.esprit.entities.SousCommentaire;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.services.eyaservice.SousCommentaireService;
import tn.esprit.utils.Session;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
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

    @FXML
    private ImageView profileImage;

    private SousCommentaire sousCommentaire;
    private final SousCommentaireService sousCommentaireService = new SousCommentaireService();
    private final UserService utilisateurService = new UserService();

    public void setSousCommentaire(SousCommentaire sousCommentaire) {
        this.sousCommentaire = sousCommentaire;

        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        // Fetch the sous-commentaire owner's name using their ID
        try {
            Utilisateur user = utilisateurService.getOneById(sousCommentaire.getUtilisateurId());
            userIdLabel.setText(user.getNom());
            loadProfileImage(user);// Display the sous-commentaire owner's name
        } catch (SQLException e) {
            e.printStackTrace();
            userIdLabel.setText("Utilisateur inconnu"); // Fallback in case of an error
        }

        contentLabel.setText(sousCommentaire.getContenu());
        timeLabel.setText(sousCommentaire.getDateCreation());

        // Check if the current user is the owner of the sous-commentaire
        if (currentUser != null && currentUser.getId() == sousCommentaire.getUtilisateurId()) {
            // Enable edit/delete button if the current user is the owner
            btnEditDelete.setVisible(true);
        } else {
            // Hide the edit/delete button if the current user is not the owner
            btnEditDelete.setVisible(false);
        }
    }

    private void loadProfileImage(Utilisateur user) {
        if (user.getProfilePicture() != null) {
            // Charger l'image à partir du tableau de bytes
            ByteArrayInputStream inputStream = new ByteArrayInputStream(user.getProfilePicture());
            Image profile = new Image(inputStream);
            profileImage.setImage(profile);
        } else {
            try {
                // Load the default image from the resources
                InputStream inputStream = getClass().getResourceAsStream("/assets/7.png");
                if (inputStream == null) {
                    throw new FileNotFoundException("Default image not found at /assets/7.png");
                }
                Image defaultImage = new Image(inputStream);
                profileImage.setImage(defaultImage);
            } catch (Exception e) {
                System.err.println("Error loading default image: " + e.getMessage());
                // Optionally, set a placeholder image or leave the ImageView empty
            }
        }
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
            showAlert("Erreur", "Vous n'êtes pas autorisé à modifier ce sous-commentaire.");
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