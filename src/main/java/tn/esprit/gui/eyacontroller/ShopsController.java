package tn.esprit.gui.eyacontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import javafx.event.ActionEvent;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ShopsController {
    @FXML private GridPane shopGrid;
    @FXML private Label homeLabel;
    @FXML private Label eventLabel;
    @FXML private Label cartLabel;
    @FXML private Label parkingLabel;   // Reference to the Home label
    @FXML private TextField searchField; // Reference to the search bar
    @FXML
    private ImageView userProfileImage;

    private UserService utilisateurService = new UserService();

    @FXML
    public void initialize() throws SQLException {
        // Load all shops initially
        loadShops(null);

        // Add event listener to the search bar
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                // Filter shops by name as the user types
                List<Utilisateur> filteredShops = utilisateurService.getShopsByName(newValue);
                updateShopGrid(filteredShops);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Add event handler to the Home label
        homeLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Homepage.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        eventLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client_event_view.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cartLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/panier.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        parkingLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/parking_lot.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    @FXML
    private void handleUserProfileClick() {
        try {
            // Load the UserDashboard.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) userProfileImage.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load the user dashboard.");
        }
    }

    private void loadShops(String searchQuery) throws SQLException {
        List<Utilisateur> shops;
        if (searchQuery == null || searchQuery.isEmpty()) {
            shops = utilisateurService.getAllShops(); // Load all shops
        } else {
            shops = utilisateurService.getShopsByName(searchQuery); // Filter by name
        }
        updateShopGrid(shops);
    }

    private void updateShopGrid(List<Utilisateur> shops) {
        shopGrid.getChildren().clear(); // Clear existing shops

        int column = 0;
        int row = 0;

        for (Utilisateur shop : shops) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shop_card.fxml"));
                Pane shopCard = loader.load();

                ShopCardController controller = loader.getController();
                controller.setData(shop); // Pass only the shop object

                shopGrid.add(shopCard, column, row);

                column++;
                if (column == 3) { // 3 columns per row
                    column = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /******* filter part ***********/
    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String category = clickedButton.getText();

        try {
            List<Utilisateur> filteredShops = utilisateurService.getShopsByCategory(category);
            updateShopGrid(filteredShops);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}