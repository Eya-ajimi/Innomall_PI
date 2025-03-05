package tn.esprit.gui.azizcontroller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import tn.esprit.entities.Role;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;

import java.sql.SQLException;

public class ModifyUserController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;

    @FXML private Label prenomLabel;
    @FXML private Label categorieLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label adresseLabel;

    private Utilisateur user;
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        roleComboBox.getItems().setAll(Role.values());
        try {
            categorieComboBox.getItems().setAll(userService.getCategories());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUser(Utilisateur user) {
        this.user = user;
        populateFields();
        configureUIForRole();
    }

    private void populateFields() {
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        roleComboBox.setValue(user.getRole());
        telephoneField.setText(user.getTelephone());
        adresseField.setText(user.getAdresse());

        try {
            if(user.getIdCategorie() > 0) {
                String categorie = userService.getNomCategorieById(user.getIdCategorie());
                categorieComboBox.setValue(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureUIForRole() {
        boolean isShopOwner = user.getRole() == Role.SHOPOWNER;

        // Configuration de visibilité
        prenomLabel.setVisible(!isShopOwner);
        prenomField.setVisible(!isShopOwner);
        categorieLabel.setVisible(isShopOwner);
        categorieComboBox.setVisible(isShopOwner);
        telephoneLabel.setVisible(!isShopOwner);
        telephoneField.setVisible(!isShopOwner);
        adresseLabel.setVisible(!isShopOwner);
        adresseField.setVisible(!isShopOwner);
    }

    @FXML
    private void handleSave() {
        updateUserModel();
        try {
            if(user.getRole() == Role.SHOPOWNER) {
                userService.updateShopOwneradmin(user);
            } else {
                userService.updateStandardUseradmin(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeWindow();
    }

    private void updateUserModel() {
        user.setNom(nomField.getText());
        user.setPrenom(prenomField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleComboBox.getValue());

        if(user.getRole() == Role.SHOPOWNER) {
            try {
                user.setIdCategorie(userService.getIdCategorieByName(categorieComboBox.getValue()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            user.setTelephone(telephoneField.getText());
            user.setAdresse(adresseField.getText());
        }
    }

    private void closeWindow() {
        nomField.getScene().getWindow().hide();
    }
}