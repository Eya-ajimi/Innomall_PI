package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.Role;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
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
    private Button btn_sigUp;

    @FXML
    private Text errorMessage;

    @FXML
    private Text forget_password; // Ajout du texte "Forgot password"

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        System.out.println("Initializing SignInController...");
        btn_sigin.setOnAction(event -> handleSignIn());

        // Gestionnaire d'événements pour "Forgot password"
        forget_password.setOnMouseClicked(event -> handleForgotPassword());
    }

    @FXML
    private void handleSignIn() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            Utilisateur user = userService.signIn(email, password);
            if (user != null) {
                errorMessage.setText("Login successful!");

                // Définir l'utilisateur connecté dans la session
                Session session = Session.getInstance();
                session.setCurrentUser(user);

                // Vérifier le rôle de l'utilisateur
                Role role = user.getRole();
                String fxmlFile = "";

                if (role == Role.CLIENT) {
                    fxmlFile = "/fxml/Homepage.fxml"; // Page XML pour l'utilisateur
                } else if (role == Role.ADMIN) {
                    errorMessage.setText("Connexion réussie !");

                    // Rediriger vers le tableau de bord de l'admin
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) btn_sigin.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                    return; // Arrêter l'exécution ici pour éviter de charger une autre page
                } else if (role == Role.SHOPOWNER) {
                    fxmlFile = "/fxml/Dashboardmaria.fxml"; // Page XML pour le shopowner
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
    }

    @FXML
    private void handleSignUp() {
        try {
            // Charger le fichier FXML de la page registree.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registree.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la page chargée
            Scene scene = new Scene(root);

            // Appliquer le fichier CSS à la nouvelle scène
            scene.getStylesheets().add(getClass().getResource("/css/resgistre.css").toExternalForm());

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) btn_sigUp.getScene().getWindow();

            // Changer la scène de la fenêtre actuelle
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("An error occurred while loading the sign-up page.");
        }
    }

    // Gestionnaire d'événements pour "Forgot password"
    private void handleForgotPassword() {
        try {
            // Charger le fichier FXML de l'interface de réinitialisation du mot de passe
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ForgotPassword.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la page chargée
            Scene scene = new Scene(root);

            // Appliquer le fichier CSS à la nouvelle scène
            scene.getStylesheets().add(getClass().getResource("/css/loginmaria.css").toExternalForm());

            // Créer une nouvelle fenêtre (stage) pour afficher la scène
            Stage stage = new Stage();
            stage.setTitle("Forgot Password");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("An error occurred while loading the forgot password page.");
        }
    }
}