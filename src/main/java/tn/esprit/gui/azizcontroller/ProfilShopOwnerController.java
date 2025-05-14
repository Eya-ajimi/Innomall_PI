package tn.esprit.gui.azizcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.utils.Session;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class ProfilShopOwnerController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label welcomeLabel, nomLabel, emailLabel, categorieLabel, descriptionLabel;

    private final UserService userService = new UserService();
    private Utilisateur currentUser;

    @FXML
    public void initialize() {
        Session session = Session.getInstance();
        currentUser = session.getCurrentUser();

        if (currentUser == null) {
            welcomeLabel.setText("Aucun utilisateur connecté.");
            return;
        }

        try {
            String nomCategorie = userService.getNomCategorieById(currentUser.getIdCategorie());

            // Set user details
            welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + "!");
            nomLabel.setText(currentUser.getNom());
            emailLabel.setText(currentUser.getEmail());
            categorieLabel.setText(nomCategorie);
            descriptionLabel.setText(currentUser.getDescription());
            
            // Recharger l'utilisateur depuis la base de données pour avoir les données à jour
            Utilisateur updatedUser = userService.getOneById(currentUser.getId());
            if (updatedUser != null) {
                currentUser = updatedUser;
                reloadProfileImage(currentUser);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du chargement des informations.");
        }
    }

    private void reloadProfileImage(Utilisateur user) {
        try {
            String imagePath = user.getProfilePicture();
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = null;
                
                // Essayer d'abord de charger depuis les ressources
                try (InputStream resourceStream = getClass().getResourceAsStream(imagePath)) {
                    if (resourceStream != null) {
                        image = new Image(resourceStream);
                        if (!image.isError()) {
                            profileImageView.setImage(image);
                            return;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Erreur lors de la lecture du flux de ressources: " + e.getMessage());
                }
                
                // Si l'image n'est pas trouvée dans les ressources, essayer le chemin absolu
                String projectPath = System.getProperty("user.dir");
                String fullPath = projectPath + "/src/main/resources" + imagePath;
                File imageFile = new File(fullPath);
                
                if (imageFile.exists()) {
                    try {
                        image = new Image(imageFile.toURI().toString());
                        if (!image.isError()) {
                            profileImageView.setImage(image);
                            return;
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image depuis le fichier: " + e.getMessage());
                    }
                } else {
                    System.err.println("Fichier image non trouvé: " + fullPath);
                }
            }
            
            // Si on arrive ici, charger l'image par défaut
            loadDefaultImage();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try (InputStream defaultImageStream = getClass().getResourceAsStream("/assets/7.png")) {
            if (defaultImageStream != null) {
                Image defaultImage = new Image(defaultImageStream);
                profileImageView.setImage(defaultImage);
            } else {
                System.err.println("Image par défaut introuvable");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
        }
    }

    @FXML
    private void editProfile(ActionEvent event) {
        try {
            // Load the FXML for the edit profile window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditUserProfileshopowner.fxml"));
            Parent root = loader.load();

            // Get the controller and set the current user
            EditUserProfileshopownerController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            // Create and set the popup stage
            Stage popupStage = new Stage();
            controller.setPopupStage(popupStage); // Set the popup stage in the controller

            // Configure the popup stage
            popupStage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            popupStage.setTitle("Edit Profile");
            popupStage.setScene(new Scene(root));

            // Show the popup and wait for it to close
            popupStage.showAndWait();

            // Après la fermeture de la fenêtre d'édition, recharger l'utilisateur et l'image
            try {
                Utilisateur updatedUser = userService.getOneById(currentUser.getId());
                if (updatedUser != null) {
                    currentUser = updatedUser;
                    reloadProfileImage(currentUser);
                    
                    // Mettre à jour les autres informations
                    String nomCategorie = userService.getNomCategorieById(currentUser.getIdCategorie());
                    welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + "!");
                    nomLabel.setText(currentUser.getNom());
                    emailLabel.setText(currentUser.getEmail());
                    categorieLabel.setText(nomCategorie);
                    descriptionLabel.setText(currentUser.getDescription());
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour des informations.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'éditeur de profil.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Session.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la déconnexion.");
        }
    }

    @FXML
    public void handleManageShop(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShopManagement.fxml"));
            Parent shopManagementRoot = loader.load();
            Scene scene = new Scene(shopManagementRoot);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion de Boutique");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de gestion de boutique.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}