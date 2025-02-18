package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Product;
import tn.esprit.services.ProductService;

import java.sql.SQLException;

public class AjoutProduitController {

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField stockField;

    @FXML
    private TextField discountField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    //--------------------------------
    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {


        saveButton.setOnAction(event -> saveProduct());
        cancelButton.setOnAction(event -> closePopup());
        //cancel
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #d6d6d6; -fx-text-fill: #333333; -fx-background-radius: 4;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-background-radius: 4;"));
        cancelButton.setOnMousePressed(e -> cancelButton.setStyle("-fx-background-color: #bfbfbf; -fx-text-fill: #333333; -fx-background-radius: 4;"));
        cancelButton.setOnMouseReleased(e -> cancelButton.setStyle("-fx-background-color: #d6d6d6; -fx-text-fill: #333333; -fx-background-radius: 4;"));

        // Save
        saveButton.setOnMouseEntered(e -> saveButton.setStyle("-fx-background-color: #ff8c33; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
        saveButton.setOnMouseExited(e -> saveButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
        saveButton.setOnMousePressed(e -> saveButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
        saveButton.setOnMouseReleased(e -> saveButton.setStyle("-fx-background-color: #ff8c33; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
    }

    private void saveProduct() {
        try {
            // Validate input fields
            if (descriptionField.getText().isEmpty()) {
                showAlert("Description is required.", Alert.AlertType.ERROR);
                return;
            }

            // Parse price with validation
            float price;
            try {
                price = Float.parseFloat(priceField.getText());
                if (price < 0) {
                    showAlert("Price cannot be negative.", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid price format. Please enter a decimal number.", Alert.AlertType.ERROR);
                return;
            }

            // Parse stock with validation
            int stock;
            try {
                stock = Integer.parseInt(stockField.getText());
                if (stock < 0) {
                    showAlert("Stock cannot be negative.", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid stock format. Please enter an integer.", Alert.AlertType.ERROR);
                return;
            }

            // Parse discount ID (optional)
            Integer discountId = null;
            if (!discountField.getText().isEmpty()) {
                try {
                    discountId = Integer.parseInt(discountField.getText());
                    if (discountId < 0) {
                        showAlert("Discount ID cannot be negative.", Alert.AlertType.ERROR);
                        return;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid discount ID format. Please enter an integer or leave blank.", Alert.AlertType.ERROR);
                    return;
                }
            }

            // Create the product object
            Product product = new Product();
            product.setDescription(descriptionField.getText());
            product.setPrice(price);
            product.setStock(stock);

            // For shop_id, you might need to get it from your app's state or context
            product.setShop_id(1);  // Default value, adjust as needed

            // Set discount_id (will be null if field was empty)
            product.setDiscount_id(discountId);

            // Insert product using service
            int result = productService.insert(product);

            if (result > 0) {
                showAlert("Product added successfully!", Alert.AlertType.INFORMATION);
                closePopup();
            } else {
                showAlert("Failed to add product.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database error: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void closePopup() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}