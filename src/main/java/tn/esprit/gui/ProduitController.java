package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Discount;
import tn.esprit.entities.Product;
import tn.esprit.services.DiscountService;
import tn.esprit.services.ProductService;
import tn.esprit.services.LikedProductService;
import java.io.File;
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
    public VBox createProductCard(Product product) {
        DiscountService discountService = new DiscountService();
        LikedProductService likedProductService = new LikedProductService();
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

        // Load product image or default image
        System.out.println(product.getPhotoUrl());
        String imagePath = product.getPhotoUrl();
        Image image;

        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    image = new Image(file.toURI().toString()); // Load from local file system
                } else {
                    throw new Exception("File not found: " + imagePath);
                }
            } else {
                throw new Exception("Image path is null or empty");
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            image = new Image(getClass().getResourceAsStream("/assets/product.png")); // Load default image
        }

        // Main image container at the top with controls overlay
        StackPane imageContainer = new StackPane();
        imageContainer.setMinHeight(180);

        // Product image for the top section
        ImageView topImage = new ImageView(image);
        topImage.setFitWidth(100);
        topImage.setFitHeight(100);
        topImage.setPreserveRatio(true);

        // Icons container
        HBox iconsBox = new HBox();
        iconsBox.setAlignment(Pos.TOP_RIGHT);
        iconsBox.setSpacing(10);
        iconsBox.setPadding(new Insets(10, 10, 0, 0));

        // Edit icon
        Button editButton = new Button();
        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/edit.png")));
        editIcon.setFitHeight(16);
        editIcon.setFitWidth(16);
        editButton.setGraphic(editIcon);
        editButton.setStyle("-fx-background-color:rgba(255, 165, 0, 0.50); " +
                "-fx-background-radius: 50%; " +
                "-fx-padding: 7;");
        editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82); " +
                "-fx-background-radius: 50%; " +
                "-fx-padding: 7;"));
        editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-background-radius: 50%; " +
                "-fx-padding: 7;"));

        // Delete icon
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/trash.png")));
        deleteIcon.setFitHeight(16);
        deleteIcon.setFitWidth(16);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-background-radius: 50%; " +
                "-fx-padding: 7;");
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color:rgba(191, 226, 246, 0.82);" +
                "-fx-background-radius: 50%; " +
                "-fx-padding: 7;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color:rgba(255, 165, 0, 0.50); " +
                "-fx-background-radius: 50%; " +
                "-fx-padding: 7;"));

        iconsBox.getChildren().addAll(editButton, deleteButton);

        // Add elements to image container with proper stacking
        imageContainer.getChildren().addAll(topImage, iconsBox);

        StackPane.setAlignment(iconsBox, Pos.TOP_CENTER);

        // Product body - CHANGED FROM BLUE TO ORANGE
        VBox productBody = new VBox();
        productBody.setSpacing(8);
        productBody.setStyle("-fx-background-color: rgba(255, 165, 0, 0.45); " +
                "-fx-padding: 15 15 10 15; " +
                "-fx-background-radius: 0 0 15px 15px;");

        // Add title label - NEW ADDITION
        Label titleLabel = new Label(product.getTitle());
        titleLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(220);

        Label descLabel = new Label(product.getDescription());
        descLabel.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 14px; -fx-font-weight: normal;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);

        // Price and stock in a horizontal box
        HBox detailsBox = new HBox();
        detailsBox.setSpacing(10);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        VBox priceBox = new VBox();
        priceBox.setSpacing(2);

        Label priceLabel = new Label("Price: " + product.getPrice() + " DT");
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill:#000000; -fx-font-size: 15px;");

        Label stockLabel = new Label("Stock: " + product.getStock());
        if (product.getStock() > 10) {
            stockLabel.setStyle("-fx-text-fill: #10813f; -fx-font-size: 14px;");
        } else if (product.getStock() > 0) {
            stockLabel.setStyle("-fx-text-fill: #ed8936; -fx-font-size: 14px;");
        } else {
            stockLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 14px;");
        }

        // Add likes count label
        Label likesLabel = new Label();
        try {
            int likesCount = likedProductService.countLikesByProductId(product.getId());
            likesLabel.setText("Likes: " + likesCount);
            likesLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 16px;");
            likesLabel.setGraphic(createLikeIcon());
            likesLabel.setContentDisplay(ContentDisplay.LEFT);
        } catch (SQLException e) {
            System.out.println("Error getting likes count: " + e.getMessage());
            likesLabel.setText("Likes: 0");
            likesLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 16px;");
            likesLabel.setGraphic(createLikeIcon());
            likesLabel.setContentDisplay(ContentDisplay.LEFT);
        }

        priceBox.getChildren().addAll(priceLabel, stockLabel, likesLabel);

        // Discount badge - CHANGED TO RED BACKGROUND
        Label discountLabel;
        try {
            Integer discountId = product.getDiscount_id();

            if (discountId != null) {
                Discount discount = discountService.getEntityById(discountId);
                if (discount != null) {
                    discountLabel = new Label(discount.getDiscountPercentage() + "%");
                    discountLabel.setStyle("-fx-background-color: rgba(193,3,25,0.85); " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 5 10; " +
                            "-fx-background-radius: 15;");
                } else {
                    discountLabel = new Label();
                }
            } else {
                discountLabel = new Label();
            }
        } catch (SQLException e) {
            discountLabel = new Label();
            e.printStackTrace();
        }

        // Add price box and discount badge to details box
        detailsBox.getChildren().addAll(priceBox);
        if (discountLabel.getText() != null && !discountLabel.getText().isEmpty()) {
            HBox.setMargin(discountLabel, new Insets(0, 0, 0, 10));
            detailsBox.getChildren().add(discountLabel);
        }

        // Add all elements to the product body - UPDATED TO INCLUDE TITLE
        productBody.getChildren().addAll(titleLabel, descLabel, detailsBox);

        // Add all elements to the main container
        productBox.getChildren().addAll(imageContainer, productBody);


        productBox.setOnMouseEntered(e -> {
            productBox.setStyle("-fx-background-color: white; " +
                    "-fx-border-color: #cccccc; " +
                    "-fx-border-width: 1px; " +
                    "-fx-border-radius: 15px; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                    "-fx-translate-y: -5;");
            productBody.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82); " +
                    "-fx-padding: 15 15 10 15; " +
                    "-fx-background-radius: 0 0 15px 15px;");
        });

        productBox.setOnMouseExited(e -> {
            productBox.setStyle("-fx-background-color: white; " +
                    "-fx-border-color: #cccccc; " +
                    "-fx-border-width: 1px; " +
                    "-fx-border-radius: 15px; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
            productBody.setStyle("-fx-background-color: rgba(255, 165, 0, 0.45); " +
                    "-fx-padding: 15 15 10 15; " +
                    "-fx-background-radius: 0 0 15px 15px;");
        });

        // event handlers for edit and delete buttons
        editButton.setOnAction(e -> showEditDialog(product));
        deleteButton.setOnAction(e -> showDeleteConfirmation(product));

        return productBox;
    }


    // Helper method to create like icon
    private ImageView createLikeIcon() {
        try {
            Image likeImage = new Image(getClass().getResourceAsStream("/assets/heart.png"));
            ImageView likeIcon = new ImageView(likeImage);
            likeIcon.setFitHeight(16);
            likeIcon.setFitWidth(16);
            return likeIcon;
        } catch (Exception e) {
            System.out.println("Error loading like icon: " + e.getMessage());
            return null;
        }
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
            product_copy.setTitle(product.getTitle());           // Add this line for title
            product_copy.setPhotoUrl(product.getPhotoUrl());   // Add this line for image path
            product_copy.setDescription(product.getDescription());
            product_copy.setPrice(product.getPrice());
            product_copy.setStock(product.getStock());
            product_copy.setDiscount_id(product.getDiscount_id()); // Add this line if not already there

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
            controller.setProduct(product_copy);

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