package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
    private Label emailLabel;

    @FXML
    private Label telephoneLabel;

    @FXML
    private Label adresseLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private ProgressBar pointsProgressBar; // Progress bar for points

    @FXML
    private Label pointsLabel; // Label to display points

    @FXML
    private Label winMessageLabel; // Label to display winning message

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
                    emailLabel.setText("Email: " + user.getEmail());
                    telephoneLabel.setText("Téléphone: " + user.getTelephone());
                    adresseLabel.setText("Adresse: " + user.getAdresse());

                    // Charger l'image de profil
                    loadProfileImage(user);

                    // Update points progress bar
                    updatePointsProgressBar(user.getPoints()); // Ensure this line is present
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Erreur lors du chargement des informations de l'utilisateur.");
            }
        } else {
            welcomeLabel.setText("Aucun utilisateur connecté.");
        }
    }

    // Update the progress bar and check for winning condition
    private void updatePointsProgressBar(int points) {
        System.out.println("Updating points progress bar. Points: " + points); // Debug statement

        if (points < 0) {
            System.err.println("Invalid points value: " + points);
            return;
        }

        double progress = (double) points / 2000; // Calculate progress
        pointsProgressBar.setProgress(progress); // Set progress bar value
        pointsLabel.setText("Points: " + points + "/2000"); // Update points label

        // Check if the user has won
        if (points >= 2000) {
            winMessageLabel.setVisible(true); // Show winning message

            // Show a cute popup message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Congratulations!");
            alert.setHeaderText("You've Won!");
            alert.setContentText("You've reached 2000 points! 🎉\nYou've won a 20% discount on your next purchase!");

            // Add a cute icon (optional)
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/party-horn.png"))); // Add a cute icon

            alert.showAndWait(); // Show the popup

            // Reset the user's points to 0
            try {
                Session session = Session.getInstance();
                Utilisateur currentUser = session.getCurrentUser();
                if (currentUser != null) {
                    userService.resetUserPoints(currentUser.getId()); // Reset points in the database
                    currentUser.setPoints(0); // Reset points in the session
                    updatePointsProgressBar(0); // Update the UI
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error resetting user points.");
            }
        } else {
            winMessageLabel.setVisible(false); // Hide winning message
        }
    }

    // Méthode pour charger l'image de profil
    private void loadProfileImage(Utilisateur user) {
        if (user.getProfilePicture() != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(user.getProfilePicture());
            Image profileImage = new Image(inputStream);
            profileImageView.setImage(profileImage);
        } else {
            try {
                InputStream inputStream = getClass().getResourceAsStream("/assets/7.png");
                if (inputStream == null) {
                    throw new FileNotFoundException("Default image not found at /assets/7.png");
                }
                Image defaultImage = new Image(inputStream);
                profileImageView.setImage(defaultImage);
            } catch (Exception e) {
                System.err.println("Error loading default image: " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditUserProfile.fxml"));
            Parent root = loader.load();
            EditUserProfileController editController = loader.getController();
            editController.setCurrentUser(Session.getInstance().getCurrentUser());
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
        System.out.println(message);
    }
}