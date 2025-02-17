package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entites.User;
import tn.esprit.services.UserService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.sql.SQLException;

public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button btn_sigin;

    @FXML
    private Text errorMessage;

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        System.out.println("Initializing SignInController...");
        btn_sigin.setOnAction(event -> handleSignIn());
    }

    @FXML
    private void handleSignIn() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            User user = userService.signIn(email, password);
            if (user != null) {
                errorMessage.setText("Login successful!");

                // Définir l'utilisateur connecté dans la session
                Session session = Session.getInstance();
                session.setCurrentUser(user);

                // Vérifier le rôle de l'utilisateur
                String role = user.getRole();
                String fxmlFile = "";

                if ("Utilisateur".equals(role)) {
                    fxmlFile = "/fxml/userDashboard.fxml"; // Page XML pour l'utilisateur
                } else if ("Shop Owner".equals(role)) {
                    fxmlFile = "/fxml/shopOwnerDashboard.fxml"; // Page XML pour le shopowner
                } else {
                    errorMessage.setText("Role non reconnu.");
                    return;
                }

                // Naviguer vers la page appropriée
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) btn_sigin.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                errorMessage.setText("Invalid email or password.");
            }
        } catch (SQLException | IOException e) {
            errorMessage.setText("An error occurred. Please try again.");
            e.printStackTrace();
        }
    }}