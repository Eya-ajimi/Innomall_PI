package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class ResetPasswordNewController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Text errorMessage;

    private UserService userService = new UserService();
    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void handleSaveNewPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation des champs
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            // Mettre à jour le mot de passe dans la base de données
            userService.updatePassword(userEmail, newPassword);
            showError("Password updated successfully!");

            // Rediriger vers la page de connexion
            redirectToLoginPage();
        } catch (SQLException e) {
            showError("An error occurred. Please try again.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setStyle("-fx-fill: #ff0000;"); // Rouge pour les erreurs
    }

    private void redirectToLoginPage() {
        try {
            // Charger la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) newPasswordField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Error loading login page.");
            e.printStackTrace();
        }
    }
}