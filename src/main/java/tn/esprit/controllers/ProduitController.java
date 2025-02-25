package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tn.esprit.entities.Discount;
import tn.esprit.entities.Product;
import tn.esprit.entities.Product;
import tn.esprit.services.DiscountService;
import tn.esprit.services.ProductService;
import tn.esprit.services.ProductService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
            productContainer.getChildren().clear(); // Clear old products
            List<Product> products = productService.getProductsByShopId(1);
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
        DiscountService discountService = new DiscountService();
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

        // Product header
        HBox productHeader = new HBox();
        productHeader.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-border-radius: 15px 15px 0 0; " +
                "-fx-background-radius: 15px 15px 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");
        productHeader.setAlignment(Pos.CENTER_LEFT);

        Label idLabel = new Label("ID: " + product.getId());
        idLabel.setStyle(" -fx-text-fill: #000000; -fx-font-size: 15px;");

        // Icons container
        HBox iconsBox = new HBox();
        iconsBox.setAlignment(Pos.CENTER_RIGHT);
        iconsBox.setSpacing(5);
        iconsBox.setPadding(new Insets(0, 10, 0, 0));

        // Edit icon
        Button editButton = new Button();
        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/edit.png")));
        editIcon.setFitHeight(13);
        editIcon.setFitWidth(13);
        editButton.setGraphic(editIcon);
        editButton.setStyle("-fx-background-color: transparent;");
        editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82);"));
        editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: transparent;"));

        // Delete icon
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/trash.png")));
        deleteIcon.setFitHeight(13);
        deleteIcon.setFitWidth(13);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: transparent;");
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82);"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: transparent;"));

        iconsBox.getChildren().addAll(editButton, deleteButton);

        // Properly adding elements to productHeader
        HBox.setHgrow(iconsBox, Priority.ALWAYS);
        productHeader.getChildren().addAll(idLabel, iconsBox);

        // Product body
        VBox productBody = new VBox();
        productBody.setStyle("-fx-padding: 15;");
        Label descLabel = new Label("Description: " + product.getDescription());
        descLabel.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 14px;");
        descLabel.setWrapText(true);
        productBody.getChildren().add(descLabel);

        // Product footer
        VBox productFooter = new VBox();
        productFooter.setStyle("-fx-background-color: rgb(255,255,255); " +
                "-fx-border-radius: 0 0 15px 15px; " +
                "-fx-background-radius: 0 0 15px 15px; " +
                "-fx-padding: 10 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1 0 0 0;");
        productFooter.setMinHeight(5);

        Label priceLabel = new Label("Price: " + product.getPrice() + " DT");
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill:#000000; -fx-font-size: 16px;");

        Label stockLabel = new Label("Stock: " + product.getStock());
        if (product.getStock() > 10) {
            stockLabel.setStyle("-fx-text-fill: #10813f; -fx-font-size: 14px;");
        } else if (product.getStock() > 0) {
            stockLabel.setStyle("-fx-text-fill: #ed8936; -fx-font-size: 14px;");
        } else {
            stockLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 14px;");
        }
        Label discountLabel;
        try {
            Discount discount = discountService.getEntityById(product.getDiscount_id());
            discountLabel = new Label("Promo " + (discount != null ? discount.getDiscountPercentage() + "%" : "Aucune promo"));
        } catch (SQLException e) {
            discountLabel = new Label("Promo: Erreur");
            e.printStackTrace(); // Log the error for debugging
        }

        priceLabel.setStyle("-fx-text-fill:#000000; -fx-font-size: 14px;");
        productFooter.getChildren().addAll(priceLabel, stockLabel,discountLabel);

        // Add all elements to the main container
        productBox.getChildren().addAll(productHeader, productBody, productFooter);

        // Hover effect
        productBox.setOnMouseEntered(e -> productBox.setStyle(productBox.getStyle() +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                "-fx-translate-y: -5;"));

        productBox.setOnMouseExited(e -> productBox.setStyle(productBox.getStyle().replace(
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                        "-fx-translate-y: -5;",
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);")));
        // event handlers for edit and delete buttons
        editButton.setOnAction(e -> showEditDialog(product));
        deleteButton.setOnAction(e -> showDeleteConfirmation(product));
        return productBox;
    }
    private void showEditDialog(Product product) {
        if (product == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun produit n'est sélectionné.");
            return;
        }
        try {
            // Create a copy of the product to edit
            Product product_copy = new Product();
            product_copy.setId(product.getId());
            product_copy.setShop_id(product.getShop_id());
            product_copy.setDescription(product.getDescription());
            product_copy.setPrice(product.getPrice());
            product_copy.setStock(product.getStock());

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ModificationProduit.fxml"));
            // Create the dialog Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier un Produit");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            // Set the scene
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            // Get the controller
            ModificationProduitController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProduct(product);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            // If user clicked save, refresh the product list
            if (controller.isOkClicked()) {
                loadProducts(); // Refresh product list
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ouverture de la fenêtre");
            alert.setContentText("Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            alert.showAndWait();
        }
    }


    private void showDeleteConfirmation(Product product) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer le produit");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer le produit: " + product.getDescription() + "?");

        // Add custom styling to match app theme (if style.css exists)
        try {
            DialogPane dialogPane = confirmDialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/tn/esprit/gui/css/style.css").toExternalForm());
        } catch (Exception e) {
            // If styling fails, continue without custom styling
        }

        // Show the confirmation dialog and wait for response
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete the product
                    ProductService productService = new ProductService();

                    // The delete method takes a Product object, not just an ID
                    // If your service has a deleteById method, use that instead
                    int result = productService.delete(product);  // Changed from productService.delete(product.getId())

                    if (result > 0) {
                        // Refresh product list
                        loadProducts();

                        // Show success message
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Succès");
                        successAlert.setHeaderText("Produit supprimé");
                        successAlert.setContentText("Le produit a été supprimé avec succès.");
                        successAlert.showAndWait();
                    } else {
                        // Show error message
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Erreur");
                        errorAlert.setHeaderText("Échec de suppression");
                        errorAlert.setContentText("La suppression du produit a échoué. Veuillez réessayer.");
                        errorAlert.showAndWait();
                    }
                } catch (SQLException e) {
                    // Show database error
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText("Erreur de base de données");
                    errorAlert.setContentText("Une erreur est survenue lors de la suppression: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }
    //-------------------------------------------------------
    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to call the product delete service
    private int deleteProduct(Product product) throws SQLException {
        // Assuming you have a service or DAO instance
        ProductService productService = new ProductService();
        return productService.delete(product);
    }


    private void showAddProductPopup() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutProduit.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ajouter un produit");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
            loadProducts();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }


}