package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Product;
import tn.esprit.services.ProductService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    @FXML
    private Button uploadImageButton;

    @FXML
    private Label imagePathLabel;

    private String imagePath;
    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> saveProduct());
        cancelButton.setOnAction(event -> closePopup());
        uploadImageButton.setOnAction(event -> uploadImage());
    }

    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                Path destinationDir = Path.of("resources/assets/product_images");
                if (!Files.exists(destinationDir)) {
                    Files.createDirectories(destinationDir);
                }

                Path destinationPath = destinationDir.resolve(file.getName());
                Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                imagePath = destinationPath.toString();
                imagePathLabel.setText("Image: " + file.getName());
            } catch (IOException e) {
                showAlert("Erreur lors de l'upload de l'image.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void saveProduct() {
        try {
            if (descriptionField.getText().isEmpty()) {
                showAlert("Remplissez le champ de description.", Alert.AlertType.ERROR);
                return;
            }

            float price;
            try {
                price = Float.parseFloat(priceField.getText());
                if (price < 0) {
                    showAlert("Entrez une valeur positive.", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Format de prix invalide. Entrez une valeur décimale.", Alert.AlertType.ERROR);
                return;
            }

            int stock;
            try {
                stock = Integer.parseInt(stockField.getText());
                if (stock < 0) {
                    showAlert("Entrez une valeur positive.", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Format de stock invalide. Entrez un nombre entier.", Alert.AlertType.ERROR);
                return;
            }

            Integer discountId = null;
            if (!discountField.getText().isEmpty()) {
                try {
                    discountId = Integer.parseInt(discountField.getText());
                    if (discountId < 0) {
                        showAlert("Entrez une valeur positive.", Alert.AlertType.ERROR);
                        return;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Format invalide pour la remise. Entrez un entier ou laissez vide.", Alert.AlertType.ERROR);
                    return;
                }
            }

            Product product = new Product();
            product.setDescription(descriptionField.getText());
            product.setPrice(price);
            product.setStock(stock);
            product.setShop_id(1);
            product.setDiscount_id(discountId);
            product.setPhotoUrl(imagePath);

            int result = productService.insert(product);

            if (result > 0) {
                showAlert("Produit ajouté avec succès!", Alert.AlertType.INFORMATION);
                closePopup();
            } else {
                showAlert("Échec de l'ajout du produit.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Erreur de base de données: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void closePopup() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Succès" : "Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
