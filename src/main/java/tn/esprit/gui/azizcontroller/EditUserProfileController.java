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
                // Read the image file
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                if (bufferedImage != null) {
                    // Convert to JavaFX Image
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    profileImageView.setImage(image);
                    profileImage = image;
                    
                    // Copy the selected image to profile_pictures folder if it's not already there
                    File targetDir = new File(projectPath + "/src/main/resources/assets/profile_pictures");
                    if (!targetDir.exists()) {
                        targetDir.mkdirs();
                    }
                    
                    File targetFile = new File(targetDir, selectedFile.getName());
                    if (!targetFile.exists()) {
                        Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    
                    // Show success message
                    showSuccess("Image de profil mise à jour avec succès.");
                } else {
                    showError("Impossible de lire l'image sélectionnée.");
                }
            } catch (IOException e) {
                showError("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
    }

    // Helper method to convert File to byte array
    private byte[] fileToByteArray(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        }
        return bytes;
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

            // Mettre à jour l'image de profil
            if (profileImage != null) {
                currentUser.setProfilePicture(imageToByteArray(profileImage));
            }

            // Vérifier si l'utilisateur souhaite changer son mot de passe
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Only process password change if any of the password fields are filled
            if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                // Verify that all password fields are filled
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showError("Veuillez remplir tous les champs de mot de passe.");
                    return;
                }

                // Verify current password
                if (!userService.verifyPassword(currentUser.getId(), currentPassword)) {
                    showError("Le mot de passe actuel est incorrect.");
                    return;
                }

                // Verify new passwords match
                if (!newPassword.equals(confirmPassword)) {
                    showError("Les nouveaux mots de passe ne correspondent pas.");
                    return;
                }

                // Set new password
                currentUser.setMotDePasse(newPassword);
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