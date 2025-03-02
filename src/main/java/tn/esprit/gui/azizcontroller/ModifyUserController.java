package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tn.esprit.entities.Role;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import java.sql.SQLException;

public class ModifyUserController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<Role> roleComboBox; // Changement de TextField à ComboBox<Role>
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField statutField;

    private Utilisateur user;

    @FXML
    public void initialize() {
        // Initialiser la ComboBox avec les valeurs de l'énumération Role
        roleComboBox.getItems().setAll(Role.values());
    }

    public void setUser(Utilisateur user) {
        this.user = user;
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        roleComboBox.setValue(user.getRole()); // Définir la valeur sélectionnée
        telephoneField.setText(user.getTelephone());
        adresseField.setText(user.getAdresse());
        statutField.setText(user.getStatut());
    }

    @FXML
    private void handleSave() {
        // Mettre à jour les informations de l'utilisateur
        user.setNom(nomField.getText());
        user.setPrenom(prenomField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleComboBox.getValue()); // Récupérer la valeur de la ComboBox
        user.setTelephone(telephoneField.getText());
        user.setAdresse(adresseField.getText());
        user.setStatut(statutField.getText());

        try {
            UserService userService = new UserService();
            userService.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        nomField.getScene().getWindow().hide();
    }
}