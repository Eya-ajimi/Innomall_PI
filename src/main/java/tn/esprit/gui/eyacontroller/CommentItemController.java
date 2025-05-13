package tn.esprit.gui.eyacontroller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import tn.esprit.entities.Commentaire;
import tn.esprit.entities.SousCommentaire;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.eyaservice.CommentaireService;
import tn.esprit.services.eyaservice.SousCommentaireService;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.utils.Session;

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
    @FXML
    private ImageView profileImage;

    private final SousCommentaireService sousCommentaireService = new SousCommentaireService();
    private final CommentaireService commentaireService = new CommentaireService();
    private final UserService utilisateurService = new UserService();
    public void setCommentData(Commentaire commentaire) {
        this.commentaire = commentaire;

        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        // Fetch the comment owner's name using their ID
        Utilisateur user = null;
        try {
            user = utilisateurService.getOneById(commentaire.getUtilisateurId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (user != null) {
            commentUser.setText(user.getNom());
            loadProfileImage(user);
        } else {
            commentUser.setText("Utilisateur inconnu");
            // optionally clear or set a default avatar
            profileImage.setImage(null);
        }

        commentContent.setText(commentaire.getContenu());
        commentTime.setText(commentaire.getDateCreation());

        // Show edit/delete only if session user owns this comment
        boolean isOwner = currentUser != null
                && currentUser.getId() == commentaire.getUtilisateurId();
        editDeleteButton.setVisible(isOwner);
        if (isOwner) {
            setupEditDeleteMenu();
        }

        // Load replies
        try {
            loadSousCommentaires();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Reply action
        btnEnvoyer.setOnAction(evt -> addSousCommentaire());
    }


    private Commentaire commentaire;

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
        if (contenu.isEmpty()) {
            return;
        }

        // Check for bad words
        if (BadWordChecker.containsBadWords(contenu)) {
            showAlert("Erreur", "Le sous-commentaire contient des mots inappropriés.", Alert.AlertType.ERROR);
            return;
        }

        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.", Alert.AlertType.ERROR);
            return;
        }

        try {
            SousCommentaire sousCommentaire = new SousCommentaire();
            sousCommentaire.setContenu(contenu);
            sousCommentaire.setCommentaireId(commentaire.getId());
            sousCommentaire.setUtilisateurId(currentUser.getId()); // Use the current user's ID
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            showAlert("Erreur", "Vous n'êtes pas autorisé à modifier ce commentaire.", Alert.AlertType.ERROR);
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
                showAlert("Erreur", "Vous n'êtes pas autorisé à supprimer ce commentaire.", Alert.AlertType.ERROR);
            }
        }
    }
}