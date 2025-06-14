package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;

import java.sql.SQLException;

public class ModifyPopupController {

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

    private Utilisateur currentUser;

    public void setUser(Utilisateur user) {
        this.currentUser = user;
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        telephoneField.setText(user.getTelephone());
        adresseField.setText(user.getAdresse());
    }

    @FXML
    private void handleSave() {
        // Mettre à jour les informations de l'utilisateur
        currentUser.setNom(nomField.getText());
        currentUser.setPrenom(prenomField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setTelephone(telephoneField.getText());
        currentUser.setAdresse(adresseField.getText());

        // Sauvegarder les modifications dans la base de données
        UserService userService = new UserService();
        try {
            userService.update(currentUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fermer la popup
        nomField.getScene().getWindow().hide();
    }

}