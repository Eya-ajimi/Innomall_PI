package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.entites.User;
import tn.esprit.services.UserService;
import tn.esprit.utils.Session;

import java.sql.SQLException;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label telephoneLabel;

    @FXML
    private Label adresseLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label statutLabel;

    @FXML
    private Label dateInscriptionLabel;

    public void initialize() {
        // Récupérer l'utilisateur connecté depuis la session
        Session session = Session.getInstance();
        User currentUser = session.getCurrentUser();

        if (currentUser != null) {
            // Afficher un message de bienvenue avec le nom de l'utilisateur
            welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + "!");

            // Récupérer les informations de l'utilisateur à partir de son ID
            UserService userService = new UserService();
            try {
                User user = userService.getOneById(currentUser.getId());

                // Afficher les informations de l'utilisateur
                if (user != null) {
                    nomLabel.setText("Nom: " + user.getNom());
                    prenomLabel.setText("Prénom: " + user.getPrenom());
                    emailLabel.setText("Email: " + user.getEmail());
                    telephoneLabel.setText("Téléphone: " + user.getTelephone());
                    adresseLabel.setText("Adresse: " + user.getAdresse());
                    roleLabel.setText("Rôle: " + user.getRole());
                    statutLabel.setText("Statut: " + user.getStatut());
                    dateInscriptionLabel.setText("Date d'inscription: " + user.getDateInscription().toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            welcomeLabel.setText("Aucun utilisateur connecté.");
        }
    }

    @FXML
    private void handleLogout() {
        // Déconnecter l'utilisateur
        Session session = Session.getInstance();
        session.logout();

        // Rediriger vers la page de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}