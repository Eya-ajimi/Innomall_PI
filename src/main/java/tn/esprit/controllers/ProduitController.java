package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Product;
import tn.esprit.services.ProductService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ProduitController {
    @FXML
    private FlowPane productContainer;

    @FXML
    private TextField searchField;

    private final ProductService productService = new ProductService();

    @FXML
    private Button addButton;

    @FXML
    public void initialize() {

        try {
            List<Product> products = productService.getProductsByShopId(1);
            loadProducts();

            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                productContainer.getChildren().clear();
                try {
                    List<Product> filteredProducts = productService.getProductsByShopId(1).stream()
                            .filter(p -> p.getDescription().toLowerCase().contains(newValue.toLowerCase()))
                            .toList();
                    displayProducts(filteredProducts);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
        }
        addButton.setOnAction(event -> showAddProductPopup());
        // Button hover & click effects
        addButton.setOnMouseEntered(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseExited(event -> addButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff791f; -fx-background-radius: 4;"));
        addButton.setOnMousePressed(event -> addButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseReleased(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));

    }

    private void loadProducts() {
        try {
            List<Product> products = productService.showAll();
            displayProducts(products);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayProducts(List<Product> products) {
        for (Product product : products) {
            VBox productBox = createProductCard(product);
            productContainer.getChildren().add(productBox);
        }
    }

    private VBox createProductCard(Product product) {
        VBox productBox = new VBox();

        productBox.setPrefWidth(250);
        productBox.setMaxWidth(250);
        productBox.setMinHeight(150);
        productBox.getStyleClass().add("product-card");
        productBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 15px; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

        // Product header with ID
        VBox productHeader = new VBox();
        productHeader.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-border-radius: 15px 15px 0 0; " +
                "-fx-background-radius: 15px 15px 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");


        Label idLabel = new Label("ID: " + product.getId());
        idLabel.setStyle(" -fx-text-fill: #000000; -fx-font-size: 15px;");
        productHeader.getChildren().add(idLabel);

        // Product body with description
        VBox productBody = new VBox();
        productBody.setStyle("-fx-padding: 15;");

        Label descLabel = new Label("Description: " + product.getDescription());
        descLabel.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 14px;"); // Increased font size
        descLabel.setWrapText(true);
        productBody.getChildren().add(descLabel);

// Product footer with price and stock
        VBox productFooter = new VBox();
        productFooter.setStyle("-fx-background-color: rgb(255,255,255); " +
                "-fx-border-radius: 0 0 15px 15px; " +
                "-fx-background-radius: 0 0 15px 15px; " +
                "-fx-padding: 10 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1 0 0 0;");
        productFooter.setMinHeight(5);  // Set minHeight to avoid excess space

        Label priceLabel = new Label("Price: " + product.getPrice() + " DT");
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill:#000000; -fx-font-size: 16px;"); // Increased font size

        Label stockLabel = new Label("Stock: " + product.getStock());

// Change stock label color based on stock level
        if (product.getStock() > 10) {
            stockLabel.setStyle("-fx-text-fill: #10813f; -fx-font-size: 14px;"); // Green for high stock, increased font size
        } else if (product.getStock() > 0) {
            stockLabel.setStyle("-fx-text-fill: #ed8936; -fx-font-size: 14px;"); // Orange for low stock, increased font size
        } else {
            stockLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 14px;"); // Red for out of stock, increased font size
        }


        productFooter.getChildren().addAll(priceLabel, stockLabel);

        // Add all sections to the product card
        productBox.getChildren().addAll(productHeader, productBody, productFooter);

        // Add hover effect
        productBox.setOnMouseEntered(e -> {
            productBox.setStyle(productBox.getStyle() +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                    "-fx-translate-y: -5;");
        });

        productBox.setOnMouseExited(e -> {
            productBox.setStyle(productBox.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                    "-fx-translate-y: -2;", "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        });

        return productBox;
    }

    private void showAddProductPopup() {
        try {
            // Change this line
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutProduit.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ajouter un produit");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            // If you need to refresh the product list after adding a new product
            loadProducts();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }


}