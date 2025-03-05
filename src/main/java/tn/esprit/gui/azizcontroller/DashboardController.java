package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private AnchorPane mainPane; // Ensure this matches the fx:id in FXML

    @FXML
    private StackPane contentPane; // Ensure this matches the fx:id in FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger la vue des statistiques dès le démarrage
        showDashboardContent();
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load(); // Utilisez Parent pour une compatibilité avec tous les types de nœuds
            contentPane.getChildren().setAll(view); // Remplace le contenu actuel par la nouvelle vue
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue : " + fxmlFile);
        }
    }

    @FXML
    private void showDashboardContent() {
        loadView("/fxml/statistiques_inscriptions.fxml");
    }

    @FXML
    private void showusersmanagement() {
        loadView("/fxml/adminDashboard.fxml");
    }

    @FXML
    private void showreclamationmanagement() {
        loadView("/fxml/admin_reclamation.fxml");
    }

    @FXML
    private void handleLogout() {
        // Déconnecter l'utilisateur (si vous avez une classe Session)
        // Session session = Session.getInstance();
        // session.logout();

        // Rediriger vers la page de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) mainPane.getScene().getWindow(); // Utilisez mainPane pour obtenir la fenêtre actuelle
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la déconnexion : " + e.getMessage());
        }
    }
}