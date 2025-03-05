package tn.esprit.gui.eyacontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.services.eyaservice.ProduitService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ProduitsController {

    @FXML private Label shopNameLabel;
    @FXML private GridPane produitsContainer; // Change VBox to GridPane

    private int shopId;
    private ProduitService produitService = new ProduitService();

    // Add filter fields and button
    @FXML private TextField searchField;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private Button applyFiltersButton;

    public void setShopId(int shopId) {
        this.shopId = shopId;
        loadProduits();
    }

    @FXML private Label homeLabel;

    @FXML
    private void loadProduits() {
        produitsContainer.getChildren().clear(); // Clear the container before loading new products

        try {
            List<Produit> produits = produitService.getProduitsByShopId(shopId);

            if (produits.isEmpty()) {
                // Display a big message when no products are found
                Label noProductsLabel = new Label("No products available for this shop.");
                noProductsLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #666;");
                produitsContainer.add(noProductsLabel, 0, 0); // Add the label to the GridPane
                GridPane.setColumnSpan(noProductsLabel, 3); // Span across all columns
                GridPane.setHalignment(noProductsLabel, javafx.geometry.HPos.CENTER); // Center the label
                System.out.println("No products found for shopId: " + shopId);
            } else {
                System.out.println("Loading " + produits.size() + " products for shopId: " + shopId);

                int column = 0;
                int row = 0;
                for (Produit produit : produits) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProduitCard.fxml"));
                    VBox produitCard = loader.load();

                    ProduitCardController controller = loader.getController();
                    controller.setData(produit);

                    // Add the product card to the GridPane
                    produitsContainer.add(produitCard, column, row);

                    // Update column and row for the next product
                    column++;
                    if (column > 2) { // 3 columns per row
                        column = 0;
                        row++;
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        // Add event listener for the "Apply Filters" button
        applyFiltersButton.setOnAction(event -> applyFilters());

        // Add event listener for the search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

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

    /******* Filter Part ***********/
    private void applyFilters() {
        produitsContainer.getChildren().clear(); // Clear the container before loading new products

        try {
            String searchQuery = searchField.getText().trim();
            double minPrice = minPriceField.getText().isEmpty() ? 0 : Double.parseDouble(minPriceField.getText());
            double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());

            System.out.println("Applying filters: searchQuery=" + searchQuery + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice);

            List<Produit> produits = produitService.getProduitsByShopIdAndFilters(shopId, searchQuery, minPrice, maxPrice);

            if (produits.isEmpty()) {
                // Display a big message when no products are found
                Label noProductsLabel = new Label("No products available for this shop.");
                noProductsLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #666;");
                produitsContainer.add(noProductsLabel, 0, 0); // Add the label to the GridPane
                GridPane.setColumnSpan(noProductsLabel, 3); // Span across all columns
                GridPane.setHalignment(noProductsLabel, javafx.geometry.HPos.CENTER); // Center the label
                System.out.println("No products found for shopId: " + shopId);
            } else {
                System.out.println("Loading " + produits.size() + " products for shopId: " + shopId);

                int column = 0;
                int row = 0;
                for (Produit produit : produits) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProduitCard.fxml"));
                    VBox produitCard = loader.load();

                    ProduitCardController controller = loader.getController();
                    controller.setData(produit);

                    // Add the product card to the GridPane
                    produitsContainer.add(produitCard, column, row);

                    // Update column and row for the next product
                    column++;
                    if (column > 2) { // 3 columns per row
                        column = 0;
                        row++;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid price input: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException | IOException e) {
            System.err.println("Error applying filters: " + e.getMessage());
            e.printStackTrace();
        }
    }
}