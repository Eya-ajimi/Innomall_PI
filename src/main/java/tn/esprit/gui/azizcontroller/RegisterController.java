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
import okhttp3.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterController {

    // Champs FXML
    @FXML
    private Label nomLabel;

    @FXML
    private Label passwordRequirementsLabel;
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
        passwordRequirementsLabel.setText(" mot de passe ");
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
    private boolean isValidPassword(String password) {
        // Au moins 8 caractères
        if (password.length() < 8) {
            return false;
        }

        // Au moins une majuscule
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return false;
        }

        // Au moins une minuscule
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return false;
        }

        // Au moins un chiffre
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return false;
        }

        // Au moins un caractère spécial
        if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) {
            return false;
        }

        return true;
    }
    @FXML
    public void handleRegister(ActionEvent event) {
        // Récupérer les valeurs des champs du formulaire
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();
        String selectedCategorie = categorieCombo.getValue();

        // Validation des champs obligatoires
        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || getSelectedRole() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs obligatoires doivent être remplis !");
            return;
        }

        // Validation du mot de passe
        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial !");
            return;
        }

        // Validation spécifique au rôle
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

        // Validation de l'email
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une adresse email valide !");
            return;
        }

        // Validation du numéro de téléphone
        if (getSelectedRole() == Role.CLIENT && !isValidPhoneNumber(telephone)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un numéro de téléphone valide (8 chiffres minimum) !");
            return;
        }

        // Créer l'objet Utilisateur
        Utilisateur newUser;
        if (getSelectedRole() == Role.CLIENT) {
            newUser = new Utilisateur(nom, prenom, email, password, adresse, telephone, "Actif", getSelectedRole(), "");
        } else {
            newUser = new Utilisateur(nom, "", email, password, "", "", "Actif", getSelectedRole(), prenom);
            newUser.setNomCategorie(selectedCategorie);
        }

        // Enregistrer l'utilisateur dans la base de données
        try {
            userService.create(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie !");

            // Envoyer un message WhatsApp après la création du compte
            if (getSelectedRole() == Role.CLIENT) {
                String formattedPhoneNumber = formatPhoneNumber(telephone);
                String message = "Votre compte a été créé avec succès. Bienvenue, " + nom + "!";
                sendWhatsAppMessage(formattedPhoneNumber, message);
            }

            // Rediriger vers la page de connexion
            redirectToLogin();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème d'enregistrement : " + e.getMessage());
        }
    }

    // Méthode pour formater le numéro de téléphone au format international
    private String formatPhoneNumber(String phoneNumber) {
        // Supprimer les espaces et les caractères non numériques
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Si le numéro commence par +216, le laisser tel quel
        if (phoneNumber.startsWith("216")) {
            return phoneNumber;
        }

        // Si le numéro commence par 0, le remplacer par 216
        if (phoneNumber.startsWith("0")) {
            return "216" + phoneNumber.substring(1);
        }

        // Si le numéro commence par 2, 5 ou 9 (indicatifs tunisiens), ajouter 216
        if (phoneNumber.startsWith("2") || phoneNumber.startsWith("5") || phoneNumber.startsWith("9")) {
            return "216" + phoneNumber;
        }

        // Si le numéro est déjà au format international, le retourner tel quel
        return phoneNumber;
    }

    // Méthode pour envoyer un message WhatsApp via l'API UltraMsg
    private void sendWhatsAppMessage(String phoneNumber, String message) {
        OkHttpClient client = new OkHttpClient();

        // Construire le corps de la requête
        RequestBody body = new FormBody.Builder()
                .add("token", "uhust0rndctegdvd") // Remplacez par votre token UltraMsg
                .add("to", phoneNumber) // Numéro de téléphone formaté
                .add("body", message) // Message à envoyer
                .add("priority", "1") // Priorité du message (optionnel)
                .add("referenceId", "") // Référence ID (optionnel)
                .add("msgId", "") // ID du message (optionnel)
                .add("mentions", "") // Mentions (optionnel)
                .build();

        // Construire la requête HTTP POST
        Request request = new Request.Builder()
                .url("https://api.ultramsg.com/instance109011/messages/chat") // Remplacez par votre instance ID
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        // Exécuter la requête
        System.out.println("Envoi du message WhatsApp à " + phoneNumber + "...");
        System.out.println("Message : " + message);

        try (Response response = client.newCall(request).execute()) {
            // Vérifier si la requête a réussi
            if (response.isSuccessful()) {
                // Lire et afficher la réponse de l'API
                String responseBody = response.body().string();
                System.out.println("Réponse de l'API : " + responseBody);
            } else {
                // Lire et afficher la réponse en cas d'erreur
                String errorResponse = response.body().string();
                System.err.println("Échec de l'envoi du message. Code de réponse : " + response.code());
                System.err.println("Réponse de l'API : " + errorResponse);
            }
        } catch (IOException e) {
            // Gérer les exceptions réseau
            System.err.println("Erreur réseau lors de l'envoi du message WhatsApp : " + e.getMessage());
            e.printStackTrace();
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