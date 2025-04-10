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
import java.sql.SQLException;
import java.util.ResourceBundle;


public class EditUserProfileshopownerController implements Initializable {

    @FXML private TextField nomField, emailField;
    @FXML private PasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private TextArea descriptionField;
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
        categorieComboBox.setValue(user.getNomCategorie()); // Set the selected category
        descriptionField.setText(user.getDescription());

        if (user.getProfilePicture() != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(user.getProfilePicture());
            profileImage = new Image(inputStream);
            profileImageView.setImage(profileImage);
        } else {
            profileImage = new Image(getClass().getResourceAsStream("/assets/7.png"));
            profileImageView.setImage(profileImage);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load categories into the ComboBox
        try {
            categorieComboBox.getItems().addAll(userService.getCategories());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load categories.");
        }
    }


    //    @FXML
//    private void handleSave() {
//        if (currentUser == null) {
//            showAlert("Error", "No user data found.");
//            return;
//        }
//
//        currentUser.setNom(nomField.getText());
//        currentUser.setEmail(emailField.getText());
//        currentUser.setNomCategorie(categorieComboBox.getValue());
//        currentUser.setDescription(descriptionField.getText());
//
//        if (!currentPasswordField.getText().isEmpty() || !newPasswordField.getText().isEmpty()) {
//            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
//                showAlert("Error", "New passwords do not match.");
//                return;
//            }
//            currentUser.setMotDePasse(newPasswordField.getText());
//        }
//
//        if (newProfilePicture != null) {
//            currentUser.setProfilePicture(newProfilePicture);
//        }
//
//        try {
//            userService.updateshopowner(currentUser);
//            showAlert("Success", "Profile updated successfully!");
//            popupStage.close();
//        } catch (SQLException e) {
//            showAlert("Error", "Failed to update profile.");
//            e.printStackTrace();
//        }
//    }
    @FXML
    private void handleSave() {
        // Update user information
        currentUser.setNom(nomField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setNomCategorie(categorieComboBox.getValue()); // Save the selected category
        currentUser.setDescription(descriptionField.getText());
        String selectedCategory = categorieComboBox.getValue();
        currentUser.setProfilePicture(newProfilePicture);
        if (selectedCategory != null) {
            try {
                int idCategorie = userService.getIdCategorieByName(selectedCategory);
                currentUser.setIdCategorie(idCategorie); // Définir l'ID, pas seulement le nom
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Échec de la récupération de l'ID de la catégorie.");
                return;
            }
        }

        // Handle password change
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        boolean passwordUpdateRequested = !currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty();

        if (passwordUpdateRequested) {
            try {
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showAlert("Error", "All password fields must be filled.");
                    return;
                }

                if (!userService.verifyPassword(currentUser.getId(), currentPassword)) {
                    showAlert("Error", "Current password is incorrect.");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showAlert("Error", "New passwords do not match.");
                    return;
                }

                currentUser.setMotDePasse(newPassword); // Set new password
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to verify password.");
                return;
            }
        } else {
            currentUser.setMotDePasse(null); // Explicitly set password to null to avoid updating it
        }

        // Handle profile picture update
        if (profileImage != null) {
            currentUser.setProfilePicture(imageViewToByteArray(profileImageView)); // Update with new image
        } else {
            currentUser.setProfilePicture(null); // Explicitly set to null if no new image
        }

        // Debug logs
        System.out.println("Password: " + currentUser.getMotDePasse()); // Should be null if not updating
        System.out.println("Profile Picture: " + currentUser.getProfilePicture()); // Should be null if not updating

        // Save changes
        try {
            userService.updateshopowner(currentUser);
            showAlert("Success", "Profile updated successfully!");
            popupStage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update profile.");
        }
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


    @FXML
    private void handleCancel() {
        popupStage.close();
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", ".png", ".jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(popupStage); // This is where the file chooser dialog is shown

        if (selectedFile != null) {
            try {
                String filePath = selectedFile.toURI().toString();
                System.out.println("Selected file path: " + filePath); // Debug log
                Image image = new Image(filePath);
                profileImageView.setImage(image);
                profileImage = image;
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage());
            }
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void setProfileImage(Image image) {
        this.profileImage = image;
        profileImageView.setImage(image);
    }

}