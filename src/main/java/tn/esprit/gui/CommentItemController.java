package tn.esprit.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import tn.esprit.entites.Commentaire;
import tn.esprit.entites.SousCommentaire;
import tn.esprit.services.CommentaireService;
import tn.esprit.services.SousCommentaireService;

public class CommentItemController {

    @FXML
    private Label commentUser;
    @FXML
    private Label commentContent;
    @FXML
    private Label commentTime;
    @FXML
    private Button editDeleteButton;
    @FXML
    private VBox vboxSousCommentaires;
    @FXML
    private TextArea txtSousCommentaire;
    @FXML
    private Button btnEnvoyer;

    private final SousCommentaireService sousCommentaireService = new SousCommentaireService();
    private final CommentaireService commentaireService = new CommentaireService();
    private Commentaire commentaire;

    public void setCommentData(Commentaire commentaire) throws SQLException {
        this.commentaire = commentaire;

        // Mise à jour des labels
        commentUser.setText("Utilisateur " + commentaire.getUtilisateurId());
        commentContent.setText(commentaire.getContenu());
        commentTime.setText(commentaire.getDateCreation());

        // Chargement des sous-commentaires
        loadSousCommentaires();

        // Configuration du menu contextuel (Modifier/Supprimer)
        setupEditDeleteMenu();

        // Add event handler for the "Envoyer" button
        btnEnvoyer.setOnAction(event -> addSousCommentaire());
    }

    /***************** sous commentaire part ***********************/
    private void loadSousCommentaires() throws SQLException {
        // Clear any existing sous-commentaires from the VBox
        vboxSousCommentaires.getChildren().clear();

        // Fetch the list of sous-commentaires based on the current commentaire's ID
        List<SousCommentaire> sousCommentaires = sousCommentaireService.getSousCommentairesByCommentaireId(commentaire.getId());
        System.out.println(sousCommentaires);
        System.out.println(commentaire);

        // Check if there are sous-commentaires to load
        if (sousCommentaires == null || sousCommentaires.isEmpty()) {
            System.out.println("Aucun sous-commentaire à afficher.");
            return;
        }

        // Loop through each sous-commentaire and load its FXML representation
        for (SousCommentaire sc : sousCommentaires) {
            try {
                // Load the SousCommentaireItem.fxml FXML file
                System.out.println(sc);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SousCommentItem.fxml"));
                System.out.println("Loading FXML from: " + loader.getLocation());

                // Create the HBox item for the sous-commentaire
                HBox sousCommentaireItem = loader.load();

                // Get the controller of the loaded FXML and set the sous-commentaire data
                SousCommentItemController controller = loader.getController();
                controller.setSousCommentaire(sc);

                // Add the created item (HBox) to the VBox
                vboxSousCommentaires.getChildren().add(sousCommentaireItem);
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement d'un sous-commentaire : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void addSousCommentaire() {
        String contenu = txtSousCommentaire.getText().trim();
        if (!contenu.isEmpty()) {
            try {
                SousCommentaire sousCommentaire = new SousCommentaire();
                sousCommentaire.setContenu(contenu);
                sousCommentaire.setCommentaireId(commentaire.getId());
                sousCommentaire.setUtilisateurId(17); // Replace with actual user ID
                sousCommentaire.setDateCreation(java.time.LocalDateTime.now().toString());

                // Insert the sous-commentaire into the database
                sousCommentaireService.insert(sousCommentaire);

                // Reload sous-commentaires to display the new one
                loadSousCommentaires();

                // Clear the text area
                txtSousCommentaire.clear();
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'ajout du sous-commentaire : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /*************************/

    private void setupEditDeleteMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editMenuItem = new MenuItem("Modifier");
        editMenuItem.setOnAction(e -> editComment());

        MenuItem deleteMenuItem = new MenuItem("Supprimer");
        deleteMenuItem.setOnAction(e -> deleteComment());

        contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);

        editDeleteButton.setOnMouseClicked(event -> {
            contextMenu.show(editDeleteButton, event.getScreenX(), event.getScreenY());
        });
    }

    private void editComment() {
        TextInputDialog dialog = new TextInputDialog(commentaire.getContenu());
        dialog.setTitle("Modifier le commentaire");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez votre nouveau commentaire :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newContent -> {
            if (!newContent.trim().isEmpty()) {
                commentaire.setContenu(newContent);
                commentContent.setText(newContent);
                updateCommentInDatabase();
            }
        });
    }

    private void updateCommentInDatabase() {
        try {
            commentaireService.update(commentaire);
            System.out.println("Commentaire mis à jour !");
        } catch (Exception e) {
            System.err.println("Erreur mise à jour : " + e.getMessage());
        }
    }

    private void deleteComment() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer ce commentaire ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                commentaireService.delete(commentaire);

                // Supprimer le commentaire de l'affichage
                Platform.runLater(() -> {
                    commentContent.setText("[Supprimé]");
                    editDeleteButton.setDisable(true);
                    vboxSousCommentaires.getChildren().clear();
                });

                System.out.println("Commentaire supprimé !");
            } catch (Exception e) {
                System.err.println("Erreur suppression : " + e.getMessage());
            }
        }
    }
}