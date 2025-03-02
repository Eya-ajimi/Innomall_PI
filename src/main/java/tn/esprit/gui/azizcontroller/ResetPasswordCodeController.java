package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.gui.azizcontroller.ResetPasswordNewController;
import tn.esprit.services.azizservice.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class ResetPasswordCodeController {

    @FXML private TextField codeField1, codeField2, codeField3, codeField4, codeField5, codeField6;
    @FXML private Text errorMessage;

    private UserService userService = new UserService();
    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void handleVerifyCode() {
        String code = codeField1.getText() + codeField2.getText() + codeField3.getText() +
                codeField4.getText() + codeField5.getText() + codeField6.getText();

        try {
            if (userService.verifyResetCode(userEmail, code)) {
                loadNewPasswordPage();
            } else {
                errorMessage.setText("Invalid verification code");
                errorMessage.setStyle("-fx-fill: #ff0000;");
            }
        } catch (SQLException e) {
            errorMessage.setText("Database error");
            errorMessage.setStyle("-fx-fill: #ff0000;");
            e.printStackTrace();
        }
    }

    private void loadNewPasswordPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResetPasswordNew.fxml"));
            Parent root = loader.load();

            ResetPasswordNewController controller = loader.getController();
            controller.setUserEmail(userEmail);

            Stage stage = (Stage) codeField1.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            errorMessage.setText("Error loading password reset page");
            e.printStackTrace();
        }
    }
}