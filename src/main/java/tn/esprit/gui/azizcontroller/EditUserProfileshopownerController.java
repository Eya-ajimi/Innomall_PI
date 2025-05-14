package tn.esprit.gui.azizcontroller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class EditUserProfileshopownerController implements Initializable {

    @FXML private TextField nomField, emailField;
    @FXML private PasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView profileImageView;
    private final UserService userService = new UserService();
    private Utilisateur currentUser;
    private Stage popupStage;
    private byte[] newProfilePicture; // Store new image bytes if uploaded
    private Image profileImage;

    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        nomField.setText(user.getNom());
        emailField.setText(user.getEmail());
        descriptionArea.setText(user.getDescription());

        try {
            String categoryName = userService.getNomCategorieById(user.getIdCategorie());
            categorieComboBox.setValue(categoryName);
        } catch (SQLException e) {
            showError("Erreur lors du chargement de la catégorie: " + e.getMessage());
        }

        loadProfileImage(user.getProfilePicture());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize default profile pictures
        initializeDefaultProfilePictures();
        
        // Load categories into the ComboBox
        try {
            List<String> categories = userService.getCategories();
            categorieComboBox.getItems().addAll(categories);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des catégories: " + e.getMessage());
        }
    }

    private void loadProfileImage(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                profileImageView.setImage(image);
            } else {
                loadDefaultImage();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/assets/7.png"));
            profileImageView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
        }
    }

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

    @FXML
    private void handleSave() {
        try {
            // Validate required fields
            if (nomField.getText().isEmpty() || emailField.getText().isEmpty()) {
                showError("Le nom et l'email sont obligatoires.");
                return;
            }

            if (categorieComboBox.getValue() == null || categorieComboBox.getValue().isEmpty()) {
                showError("Veuillez sélectionner une catégorie.");
                return;
            }

            // Update basic information
            currentUser.setNom(nomField.getText());
            currentUser.setEmail(emailField.getText());
            
            try {
                currentUser.setIdCategorie(getCategoryId());
            } catch (SQLException e) {
                showError("Erreur avec la catégorie: " + e.getMessage());
                return;
            } catch (IllegalStateException e) {
                showError(e.getMessage());
                return;
            }
            
            currentUser.setDescription(descriptionArea.getText());

            // First, update basic information
            try {
                userService.updateshopownerBasicInfo(currentUser);
            } catch (SQLException e) {
                showError("Erreur lors de la mise à jour des informations: " + e.getMessage());
                return;
            }

            // Handle password update separately
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Only process password if any password field is filled
            if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showError("Veuillez remplir tous les champs de mot de passe.");
                    return;
                }

                try {
                    if (!userService.verifyPassword(currentUser.getId(), currentPassword)) {
                        showError("Le mot de passe actuel est incorrect.");
                        currentPasswordField.clear();
                        return;
                    }
                } catch (SQLException e) {
                    showError("Erreur lors de la vérification du mot de passe: " + e.getMessage());
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showError("Les nouveaux mots de passe ne correspondent pas.");
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                    return;
                }

                if (newPassword.length() < 6) {
                    showError("Le nouveau mot de passe doit contenir au moins 6 caractères.");
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                    return;
                }

                // Update password separately
                try {
                    userService.updateShopOwnerPassword(currentUser.getId(), newPassword);
                } catch (SQLException e) {
                    showError("Erreur lors de la mise à jour du mot de passe: " + e.getMessage());
                    return;
                }
            }

            showSuccess("Profil mis à jour avec succès");
            clearPasswordFields();
            
            if (popupStage != null) {
                popupStage.close();
            }
        } catch (Exception e) {
            showError("Une erreur inattendue s'est produite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        if (popupStage != null) {
            popupStage.close();
        }
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        
        String projectPath = System.getProperty("user.dir");
        File initialDirectory = new File(projectPath + "/src/main/resources/assets/profile_pictures");
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        } else {
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

                // Generate unique filename based on timestamp
                String timestamp = String.valueOf(System.currentTimeMillis());
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.'));
                String newFileName = "profile_" + timestamp + extension;
                File targetFile = new File(targetDir, newFileName);

                // Copy file to profile_pictures directory
                Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Update user's profile picture path
                String relativePath = "/assets/profile_pictures/" + newFileName;
                
                try {
                    // Update only the profile picture in the database
                    userService.updateProfilePicture(currentUser.getId(), relativePath);
                    
                    // Update the current user object and UI
                    currentUser.setProfilePicture(relativePath);
                    loadProfileImage(relativePath);

                    showSuccess("Image de profil mise à jour avec succès");
                } catch (SQLException e) {
                    showError("Erreur lors de la mise à jour de l'image dans la base de données: " + e.getMessage());
                    // Supprimer le fichier copié en cas d'échec
                    try {
                        Files.deleteIfExists(targetFile.toPath());
                    } catch (IOException deleteError) {
                        System.err.println("Erreur lors de la suppression du fichier temporaire: " + deleteError.getMessage());
                    }
                }
            } catch (IOException e) {
                showError("Erreur lors du chargement de l'image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private byte[] imageViewToByteArray(ImageView imageView) {
        if (imageView.getImage() == null) {
            return null;
        }

        try {
            // Convert JavaFX Image to BufferedImage
            Image image = imageView.getImage();
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

            // Compress the image to JPEG format with 70% quality
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", stream);
            return stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setProfileImage(Image image) {
        this.profileImage = image;
        profileImageView.setImage(image);
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private int getCategoryId() throws SQLException {
        String selectedCategory = categorieComboBox.getValue();
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            throw new IllegalStateException("Veuillez sélectionner une catégorie");
        }
        try {
            return userService.getIdCategorieByName(selectedCategory);
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération de la catégorie: " + e.getMessage());
        }
    }
}