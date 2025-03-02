package tn.esprit.gui.azizcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.utils.Session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class ProfilShopOwnerController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label welcomeLabel, nomLabel, emailLabel, categorieLabel, descriptionLabel;

    private final UserService userService = new UserService();
    private Utilisateur currentUser;

    @FXML
    public void initialize() {
        Session session = Session.getInstance();
        currentUser = session.getCurrentUser();

        if (currentUser == null) {
            welcomeLabel.setText("Aucun utilisateur connecté.");
            return;
        }

        try {
            String nomCategorie = userService.getNomCategorieById(currentUser.getIdCategorie());

            // Set user details
            welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + "!");
            nomLabel.setText(currentUser.getNom());
            emailLabel.setText(currentUser.getEmail());
            categorieLabel.setText(nomCategorie);
            descriptionLabel.setText(currentUser.getDescription());
            reloadProfileImage(currentUser);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du chargement des informations.");
        }
    }

    private void reloadProfileImage(Utilisateur currentUser) {
        if (currentUser.getProfilePicture() != null) {
            // Load the user's profile picture from byte array
            ByteArrayInputStream inputStream = new ByteArrayInputStream(currentUser.getProfilePicture());
            Image userImage = new Image(inputStream);
            profileImageView.setImage(userImage);
        } else {
            // Load the default profile picture
            Image defaultImage = new Image(getClass().getResourceAsStream("/assets/7.png")); // Path to default image
            profileImageView.setImage(defaultImage);
        }
    }


    @FXML
    private void editProfile(ActionEvent event) {
        try {
            // Load the FXML for the edit profile window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditUserProfileshopowner.fxml"));
            Parent root = loader.load();

            // Get the controller and set the current user and profile image
            EditUserProfileshopownerController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            // Ensure the profile image is properly set
            if (profileImageView.getImage() != null) {
                controller.setProfileImage(profileImageView.getImage());
            } else {
                // Set a default image if no image is loaded
                Image defaultImage = new Image(getClass().getResourceAsStream("/assets/7.png"));
                controller.setProfileImage(defaultImage);
            }

            // Create and set the popup stage
            Stage popupStage = new Stage();
            controller.setPopupStage(popupStage); // Set the popup stage in the controller

            // Configure the popup stage
            popupStage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            popupStage.setTitle("Edit Profile");
            popupStage.setScene(new Scene(root));

            // Show the popup and wait for it to close
            popupStage.showAndWait();

            // Refresh the UI after editing
            reloadProfileImage(currentUser);
            initialize(); // Reinitialize the controller if needed
        } catch (IOException e) {
            // Handle errors when loading the FXML
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'éditeur de profil.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Session.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la déconnexion.");
        }
    }

    @FXML
    public void handleManageShop(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShopManagement.fxml"));
            Parent shopManagementRoot = loader.load();
            Scene scene = new Scene(shopManagementRoot);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion de Boutique");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de gestion de boutique.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
