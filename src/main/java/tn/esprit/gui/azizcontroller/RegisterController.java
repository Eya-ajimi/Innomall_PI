package tn.esprit.gui.azizcontroller;

import tn.esprit.entities.Role;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.util.List;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private TextField nomField, prenomField, emailField, telephoneField, adresseField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton, utilisateurButton, shopOwnerButton;

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private Label categorieLabel;

    @FXML
    private VBox formContainer;

    @FXML
    private Label adresseLabel;

    @FXML
    private Label telephoneLabel;

    // Propriété pour stocker le rôle sélectionné
    private final ObjectProperty<Role> selectedRole = new SimpleObjectProperty<>();

    private final UserService userService = new UserService();

    // Getter pour la propriété
    public ObjectProperty<Role> selectedRoleProperty() {
        return selectedRole;
    }

    // Getter pour la valeur de la propriété
    public Role getSelectedRole() {
        return selectedRole.get();
    }

    // Setter pour la valeur de la propriété
    public void setSelectedRole(Role role) {
        selectedRole.set(role);
    }

    @FXML
    public void initialize() {
        formContainer.setVisible(false);
        loadCategories(); // Charger les catégories au démarrage
    }

    // Charger les catégories depuis la base de données
    private void loadCategories() {
        try {
            List<String> categories = userService.getCategories();
            categorieCombo.getItems().addAll(categories);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement des catégories échoué");
        }
    }

    @FXML
    public void handleRoleSelection(ActionEvent event) {
        if (event.getSource() == utilisateurButton) {
            setSelectedRole(Role.CLIENT);
            updateFormForClient();
        } else if (event.getSource() == shopOwnerButton) {
            setSelectedRole(Role.SHOPOWNER);
            updateFormForShopOwner();
        }
        formContainer.setVisible(true);
    }

    private void updateFormForClient() {
        // Clear fields
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        telephoneField.clear();
        adresseField.clear();

        // Update labels and visibility
        nomLabel.setText("Nom:");
        prenomLabel.setText("Prénom:");
        nomField.setPromptText("Entrez votre nom");
        prenomField.setPromptText("Entrez votre prénom");

        adresseLabel.setVisible(true);
        adresseField.setVisible(true);
        telephoneLabel.setVisible(true);
        telephoneField.setVisible(true);

        categorieCombo.setVisible(false);
        categorieLabel.setVisible(false);
    }

    private void updateFormForShopOwner() {
        // Clear fields
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        telephoneField.clear();
        adresseField.clear();

        // Update labels and visibility
        nomLabel.setText("Nom du magasin:");
        prenomLabel.setText("Description:");
        nomField.setPromptText("Entrez le nom du magasin");
        prenomField.setPromptText("Entrez la description");

        adresseLabel.setVisible(false);
        adresseField.setVisible(false);
        telephoneLabel.setVisible(false);
        telephoneField.setVisible(false);

        categorieCombo.setVisible(true);
        categorieLabel.setVisible(true);
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        // Get values from the form fields
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();
        String selectedCategorie = categorieCombo.getValue(); // Get the selected category

        // Common validation for all roles
        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || getSelectedRole() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs obligatoires doivent être remplis !");
            return;
        }

        // Role-specific validation
        if (getSelectedRole() == Role.CLIENT) {
            // Validation for Client
            if (prenom.isEmpty() || telephone.isEmpty() || adresse.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires pour un Client !");
                return;
            }
        } else if (getSelectedRole() == Role.SHOPOWNER) {
            // Validation for Shop Owner
            if (selectedCategorie == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une catégorie pour le Shop Owner !");
                return;
            }
        }

        // Email validation
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une adresse email valide !");
            return;
        }

        // Phone number validation (only for Client)
        if (getSelectedRole() == Role.CLIENT && !isValidPhoneNumber(telephone)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un numéro de téléphone valide (8 chiffres minimum) !");
            return;
        }

        // Create the user object
        Utilisateur newUser;
        if (getSelectedRole() == Role.CLIENT) {
            newUser = new Utilisateur(nom, prenom, email, password, adresse, telephone, "Actif", getSelectedRole(), "");
        } else {
            // For Shop Owner, reuse the prenom field for description
            newUser = new Utilisateur(nom, "", email, password, "", "", "Actif", getSelectedRole(), prenom);
            newUser.setNomCategorie(selectedCategorie);
        }

        // Save the user to the database
        try {
            userService.create(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie !");
            redirectToLogin();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème d'enregistrement : " + e.getMessage());
        }
    }

    // Validation de l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }

    // Validation du numéro de téléphone
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{8,}");
    }

    // Affichage d'une alerte
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Redirection vers l'écran de connexion
    @FXML
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