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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label telephoneLabel;

    @FXML
    private Label adresseLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label statutLabel;

    @FXML
    private Label dateInscriptionLabel;

    @FXML
    private ImageView profileImageView; // Add this line for the profile image

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur connecté depuis la session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser != null) {
            // Afficher un message de bienvenue avec le nom de l'utilisateur
            welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + "!");

            // Récupérer les informations de l'utilisateur à partir de son ID
            try {
                Utilisateur user = userService.getOneById(currentUser.getId());

                // Afficher les informations de l'utilisateur
                if (user != null) {
                    nomLabel.setText("Nom: " + user.getNom());
                    prenomLabel.setText("Prénom: " + user.getPrenom());
                    emailLabel.setText("Email: " + user.getEmail());
                    telephoneLabel.setText("Téléphone: " + user.getTelephone());
                    adresseLabel.setText("Adresse: " + user.getAdresse());
                    roleLabel.setText("Rôle: " + user.getRole());
                    statutLabel.setText("Statut: " + user.getStatut());
                    dateInscriptionLabel.setText("Date d'inscription: " + user.getDateInscription().toString());

                    // Charger l'image de profil
                    loadProfileImage(user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Erreur lors du chargement des informations de l'utilisateur.");
            }
        } else {
            welcomeLabel.setText("Aucun utilisateur connecté.");
        }
    }

    // Méthode pour charger l'image de profil
    private void loadProfileImage(Utilisateur user) {
        if (user.getProfilePicture() != null) {
            // Charger l'image à partir du tableau de bytes
            ByteArrayInputStream inputStream = new ByteArrayInputStream(user.getProfilePicture());
            Image profileImage = new Image(inputStream);
            profileImageView.setImage(profileImage);
        } else {
            try {
                // Load the default image from the resources
                InputStream inputStream = getClass().getResourceAsStream("/assets/7.png");
                if (inputStream == null) {
                    throw new FileNotFoundException("Default image not found at /assets/7.png");
                }
                Image defaultImage = new Image(inputStream);
                profileImageView.setImage(defaultImage);
            } catch (Exception e) {
                System.err.println("Error loading default image: " + e.getMessage());
                // Optionally, set a placeholder image or leave the ImageView empty
            }
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

    @FXML
    private void handleEdit() {
        try {
            // Charger la vue de modification du profil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditUserProfile.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur actuel au contrôleur de modification
            EditUserProfileController editController = loader.getController();
            editController.setCurrentUser(Session.getInstance().getCurrentUser()); // Pass the current user

            // Afficher la vue de modification
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la page de modification.");
        }
    }

    private void showError(String message) {
        // Afficher un message d'erreur (vous pouvez utiliser une boîte de dialogue ou un label)
        System.out.println(message); // À remplacer par une alerte ou un label d'erreur
    }
}