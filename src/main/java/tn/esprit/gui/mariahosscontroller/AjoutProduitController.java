package tn.esprit.gui.mariahosscontroller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.application.Platform;
import tn.esprit.entities.Produit;
import tn.esprit.services.mariahossservice.*;
import tn.esprit.utils.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import java.sql.SQLException;

public class AjoutProduitController {

    @FXML
    private TextField titleField;

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
    private Session session = Session.getInstance();

    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> saveProduct());
        cancelButton.setOnAction(event -> closePopup());
        uploadImageButton.setOnAction(event -> uploadImage());
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
            if (titleField.getText().isEmpty()) {
                showAlert("Remplissez le champ de titre.", Alert.AlertType.ERROR);
                return;
            }

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

            int discountId = 0;
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

            Produit produit = new Produit();
            produit.setNom(titleField.getText());
            produit.setDescription(descriptionField.getText());
            produit.setPrix(price);
            produit.setStock(stock);
            produit.setShopId(session.getCurrentUser().getId()); // Use current user's ID
            produit.setPromotionId(discountId);
            produit.setImage_url(imagePath);

            int result = productService.insert(produit);

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
//public class AjoutProduitController {
//
//    @FXML
//    private TextField titleField;
//
//    @FXML
//    private TextArea descriptionField;
//
//    @FXML
//    private TextField priceField;
//
//    @FXML
//    private TextField stockField;
//
//    @FXML
//    private TextField discountField;
//
//    @FXML
//    private Button saveButton;
//
//    @FXML
//    private Button cancelButton;
//
//    @FXML
//    private Button uploadImageButton;
//
//    @FXML
//    private Label imagePathLabel;
//
//    private String imagePath;
//    private final ProductService productService = new ProductService();
//    private Session session = Session.getInstance();
//    private JLanguageTool englishTool;
//    private JLanguageTool frenchTool;
//
//    @FXML
//    public void initialize() {
//        // Initialize LanguageTool for English and French
//        englishTool = new JLanguageTool(new AmericanEnglish());
//        frenchTool = new JLanguageTool(new French());
//
//        // Set up button actions
//        saveButton.setOnAction(event -> saveProduct());
//        cancelButton.setOnAction(event -> closePopup());
//        uploadImageButton.setOnAction(event -> uploadImage());
//
//        // Button hover effects
//        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #d6d6d6; -fx-text-fill: #333333; -fx-background-radius: 4;"));
//        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-background-radius: 4;"));
//        cancelButton.setOnMousePressed(e -> cancelButton.setStyle("-fx-background-color: #bfbfbf; -fx-text-fill: #333333; -fx-background-radius: 4;"));
//        cancelButton.setOnMouseReleased(e -> cancelButton.setStyle("-fx-background-color: #d6d6d6; -fx-text-fill: #333333; -fx-background-radius: 4;"));
//
//        saveButton.setOnMouseEntered(e -> saveButton.setStyle("-fx-background-color: #ff8c33; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
//        saveButton.setOnMouseExited(e -> saveButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
//        saveButton.setOnMousePressed(e -> saveButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
//        saveButton.setOnMouseReleased(e -> saveButton.setStyle("-fx-background-color: #ff8c33; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
//    }
//
//    private void uploadImage() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
//        File file = fileChooser.showOpenDialog(new Stage());
//
//        if (file != null) {
//            try {
//                Path destinationDir = Path.of("resources/assets/product_images");
//                if (!Files.exists(destinationDir)) {
//                    Files.createDirectories(destinationDir);
//                }
//
//                Path destinationPath = destinationDir.resolve(file.getName());
//                Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
//
//                imagePath = destinationPath.toString();
//                imagePathLabel.setText("Image: " + file.getName());
//            } catch (IOException e) {
//                showAlert("Erreur lors de l'upload de l'image.", Alert.AlertType.ERROR);
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void saveProduct() {
//        // Check for typos in the title (async)
//        checkForTyposAsync(titleField.getText(), titleTypoMessage -> {
//            if (titleTypoMessage != null) {
//                showAlert("Title: " + titleTypoMessage, Alert.AlertType.WARNING);
//                return;
//            }
//
//            // Check for typos in the description (async)
//            checkForTyposAsync(descriptionField.getText(), descriptionTypoMessage -> {
//                if (descriptionTypoMessage != null) {
//                    showAlert("Description: " + descriptionTypoMessage, Alert.AlertType.WARNING);
//                    return;
//                }
//
//                // Proceed with the rest of the save logic
//                Platform.runLater(() -> {
//                    try {
//                        // Validate fields
//                        if (titleField.getText().isEmpty()) {
//                            showAlert("Remplissez le champ de titre.", Alert.AlertType.ERROR);
//                            return;
//                        }
//
//                        if (descriptionField.getText().isEmpty()) {
//                            showAlert("Remplissez le champ de description.", Alert.AlertType.ERROR);
//                            return;
//                        }
//
//                        // Validate price, stock, discount, etc.
//                        // ...
//
//                        // Create and save the product
//                        Produit produit = new Produit();
//                        produit.setNom(titleField.getText());
//                        produit.setDescription(descriptionField.getText());
//                        produit.setPrix(Float.parseFloat(priceField.getText()));
//                        produit.setStock(Integer.parseInt(stockField.getText()));
//                        produit.setShopId(session.getCurrentUser().getId());
//                        produit.setPromotionId(discountField.getText().isEmpty() ? 0 : Integer.parseInt(discountField.getText()));
//                        produit.setImage_url(imagePath);
//
//                        int result = productService.insert(produit);
//
//                        if (result > 0) {
//                            showAlert("Produit ajouté avec succès!", Alert.AlertType.INFORMATION);
//                            closePopup();
//                        } else {
//                            showAlert("Échec de l'ajout du produit.", Alert.AlertType.ERROR);
//                        }
//                    } catch (SQLException e) {
//                        showAlert("Erreur de base de données: " + e.getMessage(), Alert.AlertType.ERROR);
//                        e.printStackTrace();
//                    }
//                });
//            });
//        });
//    }
//    private void closePopup() {
//        Stage stage = (Stage) cancelButton.getScene().getWindow();
//        stage.close();
//    }
//
//    private void showAlert(String message, Alert.AlertType alertType) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Succès" : "Erreur");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//
//    private void checkForTyposAsync(String text, Consumer<String> onComplete) {
//        Task<String> typoCheckTask = new Task<>() {
//            @Override
//            protected String call() throws Exception {
//                return checkForTypos(text);
//            }
//        };
//
//        typoCheckTask.setOnSucceeded(event -> {
//            String result = typoCheckTask.getValue();
//            if (result != null) { // Only call onComplete if there are typos
//                onComplete.accept(result);
//            }
//        });
//
//        typoCheckTask.setOnFailed(event -> {
//            Throwable exception = typoCheckTask.getException();
//            showAlert("Error checking for typos: " + exception.getMessage(), Alert.AlertType.ERROR);
//        });
//
//        new Thread(typoCheckTask).start();
//    }
//
//    private String checkForTypos(String text) {
//        StringBuilder errorMessage = new StringBuilder();
//
//        try {
//            // Detect language (simple heuristic: more accents → French)
//            boolean isFrench = text.matches(".*[éèêàùçôâîû].*");
//
//            List<RuleMatch> matches = isFrench ? frenchTool.check(text) : englishTool.check(text);
//
//            if (!matches.isEmpty()) {
//                errorMessage.append(isFrench ? "Problèmes en français:\n" : "Issues in English:\n");
//                for (RuleMatch match : matches) {
//                    errorMessage.append("• ").append(match.getMessage()).append("\n");
//                }
//            }
//
//            return errorMessage.length() > 0 ? errorMessage.toString() : null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error checking for typos: " + e.getMessage();
//        }
//    }
//
//}