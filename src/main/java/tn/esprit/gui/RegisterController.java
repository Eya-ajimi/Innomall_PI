package tn.esprit.gui;

import tn.esprit.entites.User;
import tn.esprit.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private TextField nomField, prenomField, emailField, telephoneField, adresseField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton, utilisateurButton, shopOwnerButton;


    @FXML
    private VBox formContainer; // Conteneur du formulaire

    private String selectedRole = ""; // Variable pour stocker le rôle sélectionné

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Cacher le formulaire au démarrage
        formContainer.setVisible(false);
    }

    @FXML
    public void handleRoleSelection(ActionEvent event) {
        // Déterminer quel bouton a été cliqué
        if (event.getSource() == utilisateurButton) {
            selectedRole = "Utilisateur";
        } else if (event.getSource() == shopOwnerButton) {
            selectedRole = "Shop Owner";
        }

        // Afficher le formulaire
        formContainer.setVisible(true);

        // Changer le style des boutons pour indiquer la sélection
        utilisateurButton.getStyleClass().remove("selected");
        shopOwnerButton.getStyleClass().remove("selected");
        if (selectedRole.equals("Utilisateur")) {
            utilisateurButton.getStyleClass().add("selected");
        } else {
            shopOwnerButton.getStyleClass().add("selected");
        }
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();

        // Validation des champs
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty() || adresse.isEmpty() || selectedRole.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une adresse email valide !");
            return;
        }

        if (!isValidPhoneNumber(telephone)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un numéro de téléphone valide (8 chiffres minimum) !");
            return;
        }

        // Création de l'utilisateur
        User newUser = new User(nom, prenom, email, password, adresse, telephone, "Actif", selectedRole);

        try {
            userService.create(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie !");
            redirectToLogin();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème d'enregistrement : " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{8,}"); // Minimum 8 chiffres
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'écran de connexion.");
            e.printStackTrace();
        }
    }

}