package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.gui.azizcontroller.ResetPasswordCodeController;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.utils.EmailService;

import java.io.IOException;
import java.sql.SQLException;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;
    @FXML
    private Text errorMessage;

    private UserService userService = new UserService();

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Please enter your email address.");
            return;
        }

        try {
            if (userService.emailExists(email)) {
                int resetCode = userService.generatePasswordResetToken(email);
                EmailService.sendPasswordResetEmail(email, resetCode);

                // Navigate to code verification page
                loadCodeVerificationPage(email);
            } else {
                showError("No account found with this email address.");
            }
        } catch (SQLException e) {
            showError("Database error. Please try again.");
            e.printStackTrace();
        } catch (RuntimeException e) {
            showError("Failed to send email. Check your connection.");
            e.printStackTrace();
        }
    }

    private void loadCodeVerificationPage(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResetPasswordCode.fxml"));
            Parent root = loader.load();

            ResetPasswordCodeController controller = loader.getController();
            controller.setUserEmail(email);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Error loading verification page");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setStyle("-fx-fill: #ff0000;");
    }
}