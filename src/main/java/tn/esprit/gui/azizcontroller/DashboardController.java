package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.utils.Session;
import java.io.IOException;

public class DashboardController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private StackPane contentPane;
    private final UserService userService = new UserService();
    private Utilisateur currentUser;

    public void initialize() {
        Session session = Session.getInstance();
        currentUser = session.getCurrentUser();
        showDashboardContent();
        // Remove the handleLogout() call from here
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboardContent() {
        loadView("/fxml/DashboardContent.fxml");
    }

    @FXML
    private void showusersmanagement() {
        loadView("/fxml/adminDashboard.fxml");
    }

    @FXML
    private void showreclamationmanagement() {
        loadView("/fxml/GestionReclamation.fxml");
    }

    @FXML
    private void showstatistics() {
        loadView("/fxml/Statiqtiques.fxml");
    }

    @FXML
    public void handleLogout() {
        Session.getInstance().logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Ensure correct stage retrieval
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la déconnexion.");
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