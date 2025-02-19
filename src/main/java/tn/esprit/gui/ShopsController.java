package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.esprit.entites.Utilisateur;
import tn.esprit.services.UtilisateurService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ShopsController {
    @FXML private GridPane shopGrid;
    @FXML private Label homeLabel; // Reference to the Home label

    private UtilisateurService utilisateurService = new UtilisateurService();
    private int utilisateurId = 15; // Replace with the actual current user ID (e.g., from session or login)

    @FXML
    public void initialize() {
        // Load shops
        try {
            List<Utilisateur> shops = utilisateurService.getAllShops();
            System.out.println("Shops retrieved: " + shops.size()); // Debugging

            int column = 0;
            int row = 0;

            for (Utilisateur shop : shops) {
                System.out.println("Adding shop: " + shop.getNom()); // Debugging

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shop_card.fxml"));
                Pane shopCard = loader.load();

                ShopCardController controller = loader.getController();
                controller.setData(shop, utilisateurId); // Pass the current user ID

                shopGrid.add(shopCard, column, row);

                column++;
                if (column == 3) { // 3 columns per row
                    column = 0;
                    row++;
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

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
    }
}