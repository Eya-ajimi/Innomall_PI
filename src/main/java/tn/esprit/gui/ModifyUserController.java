package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tn.esprit.entites.User;
import tn.esprit.services.UserService;

import java.sql.SQLException;

public class ModifyUserController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField statutField;

    private User user;

    public void setUser(User user) {
        this.user = user;
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        roleField.setText(user.getRole());
        telephoneField.setText(user.getTelephone());
        adresseField.setText(user.getAdresse());
        statutField.setText(user.getStatut());
    }

    @FXML
    private void handleSave() {
        // Mettre à jour les informations de l'utilisateur avec les valeurs des champs
        user.setNom(nomField.getText());
        user.setPrenom(prenomField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleField.getText());
        user.setTelephone(telephoneField.getText());
        user.setAdresse(adresseField.getText());
        user.setStatut(statutField.getText());

        try {
            // Mettre à jour l'utilisateur dans la base de données
            UserService userService = new UserService();
            userService.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fermer la fenêtre de modification
        nomField.getScene().getWindow().hide();
    }
}