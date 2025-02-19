package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entites.User;
import tn.esprit.services.UserService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class adminDashboardControlleur {
    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nomColumn;

    @FXML
    private TableColumn<User, String> prenomColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> telephoneColumn;

    @FXML
    private TableColumn<User, String> adresseColumn;

    @FXML
    private TableColumn<User, String> dateInscriptionColumn;

    @FXML
    private TableColumn<User, String> statutColumn;

    @FXML
    private TableColumn<User, Void> actionsColumn;

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

        // Configurer les colonnes du TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        dateInscriptionColumn.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Configurer la colonne des actions
        actionsColumn.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button modifyButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");
                    private final HBox hbox = new HBox(modifyButton, deleteButton);

                    {
                        // Style des boutons (optionnel)
                        modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                        // Espacement entre les boutons
                        hbox.setSpacing(10);

                        // Gestion des événements pour les boutons
                        modifyButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleModifyUser(user);
                        });

                        deleteButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        // Récupérer et afficher les utilisateurs
        try {
            UserService userService = new UserService();
            List<User> users = userService.getAll();
            userTable.getItems().addAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour gérer la modification d'un utilisateur
    private void handleModifyUser(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modify_user.fxml"));
            Parent root = loader.load();

            ModifyUserController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier l'utilisateur");
            stage.initModality(Modality.APPLICATION_MODAL); // Rend la fenêtre modale
            stage.showAndWait(); // Attend que la fenêtre soit fermée

            // Rafraîchir le tableau après la fermeture de la fenêtre de modification
            refreshTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour gérer la suppression d'un utilisateur
    private void handleDeleteUser(User user) {
        System.out.println("Supprimer l'utilisateur : " + user.getNom());
        try {
            UserService userService = new UserService();
            userService.delete(user.getId());
            userTable.getItems().remove(user); // Retirer l'utilisateur de la table
        } catch (SQLException e) {
            e.printStackTrace();
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
    private void refreshTable() {
        userTable.getItems().clear();
        try {
            UserService userService = new UserService();
            List<User> users = userService.getAll();
            userTable.getItems().addAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
