package tn.esprit.gui;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Discount;
import tn.esprit.entities.Product;
import tn.esprit.services.DiscountService;
import tn.esprit.services.ProductService;

public class ModificationProduitController implements Initializable {

    @FXML
    private ComboBox<Discount> discountComboBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField stockField;

    @FXML
    private TextField priceField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField titleField;

    @FXML
    private ImageView productImageView;

    @FXML
    private Button chooseImageButton;

    private String imagePath; // To store the image path

    private Product currentProduct;
    private Integer originalDiscountId; // Store the original discount_id
    private ProductService productService;
    private DiscountService discountService;
    private Stage dialogStage;
    private boolean okClicked = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productService = new ProductService();
        discountService = new DiscountService();

        // Load discount data for ComboBox
        loadDiscounts();

        // Set up button event handlers
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> dialogStage.close());

        // Setup image selection
        setupImageSelection();
    }

    /**
     * Sets up the image selection functionality
     */
    private void setupImageSelection() {
        chooseImageButton.setOnAction(event -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Sélectionner une image");
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            java.io.File selectedFile = fileChooser.showOpenDialog(dialogStage);
            if (selectedFile != null) {
                imagePath = selectedFile.getAbsolutePath();
                javafx.scene.image.Image image = new javafx.scene.image.Image(selectedFile.toURI().toString());
                productImageView.setImage(image);
            }
        });
    }

    /**
     * Load discounts for the ComboBox
     */
    private void loadDiscounts() {
        try {
            // Add a null option first (for no discount)
            ObservableList<Discount> discountList = FXCollections.observableArrayList();

            // Add a placeholder for null/no discount option
            Discount noDiscount = new Discount();
            noDiscount.setId(-1); // Use a special ID to identify "no discount" option
            noDiscount.setDiscountPercentage(0);
            discountList.add(noDiscount);

            // Add all actual discounts from database
            List<Discount> discounts = discountService.getDiscountByShopId(1);
            discountList.addAll(discounts);

            discountComboBox.setItems(discountList);

            // Set a custom cell factory to display discount name
            discountComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Discount>() {
                @Override
                protected void updateItem(Discount item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        if (item.getId() == -1) {
                            setText("Aucune promotion");
                        } else {
                            setText(+ item.getDiscountPercentage() + "%)");
                        }
                    }
                }
            });

            // Set the same converter for the ComboBox display
            discountComboBox.setButtonCell(discountComboBox.getCellFactory().call(null));

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger les promotions", e.getMessage());
        }
    }

    /**
     * Sets the product to be edited
     *
     * @param product The product to edit
     */
    public void setProduct(Product product) {
        this.currentProduct = product;

        // Store the original discount_id
        this.originalDiscountId = product.getDiscount_id();

        // Populate fields with product data
        titleField.setText(product.getTitle());
        descriptionField.setText(product.getDescription());
        stockField.setText(Integer.toString(product.getStock()));
        priceField.setText(Float.toString(product.getPrice()));

        // Load product image if available
        if (product.getPhotoUrl() != null && !product.getPhotoUrl().isEmpty()) {
            imagePath = product.getPhotoUrl();
            try {
                java.io.File imageFile = new java.io.File(imagePath);
                if (imageFile.exists()) {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(imageFile.toURI().toString());
                    productImageView.setImage(image);
                }
            } catch (Exception e) {
                System.out.println("Could not load product image: " + e.getMessage());
            }
        }

        // Set the selected discount or "No discount" if null
        try {
            if (product.getDiscount_id() == null || product.getDiscount_id() == 0 || product.getDiscount_id() == -1) {
                // Select the "No discount" option (first in the list)
                discountComboBox.getSelectionModel().select(0);
            } else {
                // Try to find and select the matching discount
                boolean found = false;
                for (Discount discount : discountComboBox.getItems()) {
                    if (discount.getId() == product.getDiscount_id()) {
                        discountComboBox.setValue(discount);
                        found = true;
                        break;
                    }
                }

                // If no matching discount found, select "No discount"
                if (!found) {
                    discountComboBox.getSelectionModel().select(0);
                }
            }
        } catch (Exception e) {
            // If any error occurs, select "No discount"
            discountComboBox.getSelectionModel().select(0);
        }
    }

    /**
     * Sets the stage for this dialog
     *
     * @param dialogStage The stage to set
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Returns whether the user clicked the save button
     *
     * @return true if save button was clicked, false otherwise
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Handle save button action
     */
    private void handleSave() {
        if (isInputValid()) {
            try {
                // Update product with form data
                Discount selectedDiscount = discountComboBox.getValue();

                if (selectedDiscount != null && selectedDiscount.getId() == -1) {
                    // If "No discount" is selected, set discount_id to null
                    currentProduct.setDiscount_id(null);
                } else if (selectedDiscount != null) {
                    // Normal discount selected
                    currentProduct.setDiscount_id(selectedDiscount.getId());
                } else {
                    // Keep the original discount_id if no selection was made
                    currentProduct.setDiscount_id(originalDiscountId);
                }

                currentProduct.setTitle(titleField.getText());
                currentProduct.setDescription(descriptionField.getText());
                currentProduct.setStock(Integer.parseInt(stockField.getText()));
                currentProduct.setPrice(Float.parseFloat(priceField.getText()));
                currentProduct.setPhotoUrl(imagePath);

                // Print debug info
                System.out.println("Updating product: " + currentProduct.getId());
                System.out.println("Original discount_id: " + originalDiscountId);
                System.out.println("New discount_id: " + currentProduct.getDiscount_id());

                // Update in database
                int result = productService.update(currentProduct);

                if (result > 0) {
                    okClicked = true;
                    dialogStage.close();
                } else {
                    showAlert(AlertType.ERROR, "Erreur", "Échec de mise à jour",
                            "La mise à jour du produit a échoué. Veuillez réessayer.");
                }
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur de base de données", e.getMessage());
                e.printStackTrace(); // Print stack trace for debugging
            }
        }
    }

    /**
     * Validates the user input in the text fields
     *
     * @return true if input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (titleField.getText() == null || titleField.getText().isEmpty()) {
            errorMessage += "Le titre ne peut pas être vide\n";
        }

        if (descriptionField.getText() == null || descriptionField.getText().isEmpty()) {
            errorMessage += "La description ne peut pas être vide\n";
        }

        if (stockField.getText() == null || stockField.getText().isEmpty()) {
            errorMessage += "Le stock ne peut pas être vide\n";
        } else {
            try {
                Integer.parseInt(stockField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Le stock doit être un nombre entier\n";
            }
        }

        if (priceField.getText() == null || priceField.getText().isEmpty()) {
            errorMessage += "Le prix ne peut pas être vide\n";
        } else {
            try {
                Float.parseFloat(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Le prix doit être un nombre décimal\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(AlertType.ERROR, "Erreur de validation",
                    "Veuillez corriger les erreurs suivantes:", errorMessage);
            return false;
        }
    }

    /**
     * Shows an alert dialog
     */
    private void showAlert(AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}