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
import javafx.stage.Window;
import tn.esprit.entities.Role;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.net.URL;
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
    private Text forget_password;

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        System.out.println("Initializing SignInController...");
        btn_sigin.setOnAction(event -> handleSignIn());
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

                Session session = Session.getInstance();
                session.setCurrentUser(user);

                Role role = user.getRole();
                String fxmlFile = "";

                if (role == Role.CLIENT) {
                    fxmlFile = "/fxml/Homepage.fxml";
                } else if (role == Role.ADMIN) {
                    errorMessage.setText("Connexion réussie !");
                    loadFXML("/fxml/Dashboard.fxml", btn_sigin.getScene().getWindow());
                    return;
                } else if (role == Role.SHOPOWNER) {
                    fxmlFile = "/fxml/shopOwnerDashboard.fxml";
                } else {
                    errorMessage.setText("Role non reconnu.");
                    return;
                }

                loadFXML(fxmlFile, btn_sigin.getScene().getWindow());
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
            URL fxmlUrl = getClass().getResource("/fxml/registree.fxml");
            if (fxmlUrl == null) {
                System.err.println("FXML file not found: /fxml/registree.fxml");
                errorMessage.setText("FXML file not found: /fxml/registree.fxml");
                return;
            }

            URL cssUrl = getClass().getResource("/css/styleuser.css");
            if (cssUrl == null) {
                System.err.println("CSS file not found: /css/style.css");
                errorMessage.setText("CSS file not found: /css/style.css");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = (Stage) btn_sigUp.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("An error occurred while loading the sign-up page.");
        }
    }

    private void handleForgotPassword() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/ForgotPassword.fxml");
            if (fxmlUrl == null) {
                System.err.println("FXML file not found: /fxml/ForgotPassword.fxml");
                errorMessage.setText("FXML file not found: /fxml/ForgotPassword.fxml");
                return;
            }

            URL cssUrl = getClass().getResource("/css/styleuser.css");
            if (cssUrl == null) {
                System.err.println("CSS file not found: /css/styleuser.css");
                errorMessage.setText("CSS file not found: /css/style.css");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Forgot Password");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("An error occurred while loading the forgot password page.");
        }
    }

    private void loadFXML(String fxmlFile, Window window) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlFile);
        if (fxmlUrl == null) {
            System.err.println("FXML file not found: " + fxmlFile);
            errorMessage.setText("FXML file not found: " + fxmlFile);
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = (Stage) window;
        stage.setScene(scene);
        stage.show();
    }
}