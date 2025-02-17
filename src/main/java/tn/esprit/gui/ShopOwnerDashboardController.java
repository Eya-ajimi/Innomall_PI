package tn.esprit.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.entites.User;
import tn.esprit.utils.Session;

public class ShopOwnerDashboardController {

    @FXML
    private Label welcomeLabel; // Champ pour afficher le message de bienvenue

    public void initialize() {
        // Récupérer l'utilisateur connecté depuis la session
        Session session = Session.getInstance();
        User currentUser = session.getCurrentUser();

        if (currentUser != null) {
            // Afficher un message de bienvenue avec le nom de l'utilisateur
            welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + "!");
        } else {
            welcomeLabel.setText("Aucun utilisateur connecté.");
        }
    }

    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        // Déconnecter l'utilisateur en réinitialisant la session
        Session session = Session.getInstance();
        session.logout();

        // Rediriger vers la page de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}