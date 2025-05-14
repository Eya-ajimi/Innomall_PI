package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class EditUserProfileController {

    @FXML
    private TextField nomField;



    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField adresseField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ImageView profileImageView;

    private UserService userService = new UserService();
    private Utilisateur currentUser;
    private Image profileImage;

    // Méthode pour définir l'utilisateur actuel
    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        // Pré-remplir les champs avec les informations de l'utilisateur
        nomField.setText(user.getNom());
        //prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        telephoneField.setText(user.getTelephone());
        adresseField.setText(user.getAdresse());

        // Charger l'image de profil
        loadProfileImage(user);
    }

    @FXML
    public void initialize() {
        // Initialize default profile pictures
        initializeDefaultProfilePictures();
    }

    // Méthode pour charger l'image de profil
    private void loadProfileImage(Utilisateur user) {
        try {
            String profilePicturePath = user.getProfilePicture();
            if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
                // Load image from the resource path
                Image image = new Image(getClass().getResourceAsStream(profilePicturePath));
                profileImageView.setImage(image);
            } else {
                // Load default image
                Image defaultImage = new Image(getClass().getResourceAsStream("/assets/7.png"));
                profileImageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            // Load default image in case of error
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/assets/7.png"));
                profileImageView.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Erreur lors du chargement de l'image par défaut : " + ex.getMessage());
            }
        }
    }

    // Méthode pour gérer le téléchargement d'une nouvelle image de profil
    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        
        // Set initial directory to project's profile_pictures folder
        String projectPath = System.getProperty("user.dir");
        File initialDirectory = new File(projectPath + "/src/main/resources/assets/profile_pictures");
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        } else {
            // If profile_pictures doesn't exist, use the assets folder
            initialDirectory = new File(projectPath + "/src/main/resources/assets");
            if (initialDirectory.exists()) {
                fileChooser.setInitialDirectory(initialDirectory);
            }
        }
        
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Create profile_pictures directory if it doesn't exist
                File targetDir = new File(projectPath + "/src/main/resources/assets/profile_pictures");
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                
                String fileName = selectedFile.getName();
                File targetFile = new File(targetDir, fileName);
                
                // Copy the file if it's not already in the target directory
                if (!targetFile.exists()) {
                    Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
                // Set the profile picture path relative to the resources folder
                String relativePath = "/assets/profile_pictures/" + fileName;
                
                // Update only the profile picture in the database
                userService.updateProfilePicture(currentUser.getId(), relativePath);
                
                // Update the current user object and UI
                currentUser.setProfilePicture(relativePath);
                Image image = new Image(getClass().getResourceAsStream(relativePath));
                profileImageView.setImage(image);
                
                showSuccess("Image de profil mise à jour avec succès.");
            } catch (IOException | SQLException e) {
                showError("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
    }

    // Method to initialize default profile pictures
    private void initializeDefaultProfilePictures() {
        String projectPath = System.getProperty("user.dir");
        File profilePicturesDir = new File(projectPath + "/src/main/resources/assets/profile_pictures");
        
        if (!profilePicturesDir.exists()) {
            profilePicturesDir.mkdirs();
            
            // Copy default profile pictures from assets to profile_pictures
            File assetsDir = new File(projectPath + "/src/main/resources/assets");
            String[] defaultPictures = {"7.png", "user.png", "user3.png"};
            
            for (String picture : defaultPictures) {
                File sourceFile = new File(assetsDir, picture);
                File targetFile = new File(profilePicturesDir, picture);
                if (sourceFile.exists() && !targetFile.exists()) {
                    try {
                        Files.copy(sourceFile.toPath(), targetFile.toPath());
                    } catch (IOException e) {
                        System.err.println("Error copying default profile picture: " + e.getMessage());
                    }
                }
            }
        }
    }

    // Méthode pour sauvegarder les modifications du profil
    @FXML
    private void handleSave() {
        try {
            // Mettre à jour les informations de base
            currentUser.setNom(nomField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setTelephone(telephoneField.getText());
            currentUser.setAdresse(adresseField.getText());

            // Gérer la mise à jour du mot de passe séparément
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Ne mettre à jour le mot de passe que si tous les champs sont remplis
            if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showError("Veuillez remplir tous les champs de mot de passe.");
                    return;
                }

                if (!userService.verifyPassword(currentUser.getId(), currentPassword)) {
                    showError("Le mot de passe actuel est incorrect.");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showError("Les nouveaux mots de passe ne correspondent pas.");
                    return;
                }

                currentUser.setMotDePasse(newPassword);
            } else {
                // Si aucun champ de mot de passe n'est rempli, on s'assure de ne pas modifier le mot de passe
                currentUser.setMotDePasse(null);
            }

            // Sauvegarder les modifications
            userService.updateUser(currentUser);
            showSuccess("Profil mis à jour avec succès.");
            redirectToDashboard();
        } catch (SQLException e) {
            showError("Erreur lors de la mise à jour du profil: " + e.getMessage());
        } catch (Exception e) {
            showError("Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }

    // Méthode pour afficher une erreur
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher un succès
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour rediriger vers le tableau de bord
    private void redirectToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour annuler et revenir au tableau de bord
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de la redirection vers le tableau de bord.");
        }
    }
}