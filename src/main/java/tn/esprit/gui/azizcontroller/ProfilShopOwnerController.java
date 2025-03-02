package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.utils.Session;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;

public class ShopOwnerDashboardController {

    @FXML
    private ImageView profileImageView; // Add this line for the profile image

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label nomLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label categorieLabel;

    @FXML
    private Label descriptionLabel;

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur connecté depuis la session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();
        System.out.println(currentUser + "azouzzzzzz");

        try {
            String nomCategorie = userService.getNomCategorieById(currentUser.getIdCategorie());
            System.out.println(currentUser.getIdCategorie() + "dhieuhdeideiuhdi ");

            if (currentUser != null) {
                // Afficher un message de bienvenue avec le nom de l'utilisateur
                welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + "!");

                // Afficher les informations de l'utilisateur
                nomLabel.setText("Nom: " + currentUser.getNom());
                emailLabel.setText("Email: " + currentUser.getEmail());
                categorieLabel.setText("Catégorie: " + nomCategorie);
                descriptionLabel.setText("Description: " + currentUser.getDescription()); // Afficher la description

                // Load the profile image
                reloadProfileImage(currentUser); // Add this line to load the profile image
            } else {
                welcomeLabel.setText("Aucun utilisateur connecté.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Add this method to load the profile image
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
    private void handleEditProfile() {
        try {
            // Charger la vue d'édition du profil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditUserProfileshopowner.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur actuel au contrôleur d'édition
            EditUserProfileshopownerController editController = loader.getController();
            editController.setCurrentUser(Session.getInstance().getCurrentUser());

            // Pass the current profile image to the edit controller
            editController.setProfileImage(profileImageView.getImage());

            // Afficher la vue d'édition
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Déconnecter l'utilisateur
        Session session = Session.getInstance();
        session.logout();

        // Rediriger vers la page de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}