package tn.esprit.gui.azizcontroller;

import javafx.embed.swing.SwingFXUtils;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;

public class EditUserProfileController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

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
        // Cette méthode est appelée lors du chargement du FXML
        // Vous pouvez l'utiliser pour des initialisations supplémentaires
    }

    // Méthode pour charger l'image de profil
    private void loadProfileImage(Utilisateur user) {
        if (user.getProfilePicture() != null) {
            // Charger l'image à partir du tableau de bytes
            ByteArrayInputStream inputStream = new ByteArrayInputStream(user.getProfilePicture());
            profileImage = new Image(inputStream);
            profileImageView.setImage(profileImage);
        } else {
            // Charger l'image par défaut
            try {
                InputStream inputStream = getClass().getResourceAsStream("/assets/7.png");
                if (inputStream == null) {
                    throw new FileNotFoundException("Image par défaut non trouvée : /assets/7.png");
                }
                profileImage = new Image(inputStream);
                profileImageView.setImage(profileImage);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image par défaut : " + e.getMessage());
                // Optionnel : définir une image de remplacement ou laisser l'ImageView vide
            }
        }
    }

    // Méthode pour gérer le téléchargement d'une nouvelle image de profil
    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                profileImageView.setImage(image);
                profileImage = image;
            } catch (Exception e) {
                showError("Erreur lors du chargement de l'image.");
            }
        }
    }

    // Méthode pour sauvegarder les modifications du profil
    @FXML
    private void handleSave() {
        // Mettre à jour les informations de base
        currentUser.setNom(nomField.getText());
        //currentUser.setPrenom(prenomField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setTelephone(telephoneField.getText());
        currentUser.setAdresse(adresseField.getText());

        // Mettre à jour l'image de profil
        if (profileImage != null) {
            currentUser.setProfilePicture(imageToByteArray(profileImage)); // Set new image
        }
        // If no new image is provided, do not modify the profilePicture field

        // Vérifier si l'utilisateur souhaite changer son mot de passe
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!currentPassword.isEmpty() && !newPassword.isEmpty() && !confirmPassword.isEmpty()) {
            try {
                if (!userService.verifyPassword(currentUser.getId(), currentPassword)) {
                    showError("Le mot de passe actuel est incorrect.");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showError("Les nouveaux mots de passe ne correspondent pas.");
                    return;
                }

                currentUser.setMotDePasse(newPassword); // Set new password
            } catch (SQLException e) {
                showError("Erreur lors de la vérification du mot de passe.");
                return;
            }
        } else {
            currentUser.setMotDePasse(null); // Explicitly set to null if not updating
        }

        // Debug logs
        System.out.println("Password: " + currentUser.getMotDePasse()); // Should be null or empty
        System.out.println("Profile Picture: " + currentUser.getProfilePicture()); // Should be null if not updating

        // Sauvegarder les modifications
        try {
            userService.updateUser(currentUser);
            showSuccess("Profil mis à jour avec succès.");
            redirectToDashboard();
        } catch (SQLException e) {
            showError("Erreur lors de la mise à jour du profil.");
        }
    }

    // Méthode pour convertir une image en tableau de bytes
    private byte[] imageToByteArray(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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