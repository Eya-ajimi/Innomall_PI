package tn.esprit.controllers;
import java.net.URL;
import java.io.IOException;
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
import tn.esprit.services.DiscountService;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import javafx.scene.control.Alert;

public class PromotionController {

    @FXML
    private FlowPane discountContainer;
    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;

    private final DiscountService discountService = new DiscountService();

    @FXML
    public void initialize() {
        try {
            List<Discount> discounts = discountService.getDiscountByShopId(1);
            loadPromotions(discounts);

            addButton.setOnAction(event -> showAddPromotionPopup());

            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                discountContainer.getChildren().clear();
                try {
                    List<Discount> filteredDiscounts;
                    if (newValue.isEmpty()) {
                        filteredDiscounts = discountService.showAll();
                    } else {
                        try {
                            float percentage = Float.parseFloat(newValue);
                            filteredDiscounts = discountService.showAll().stream()
                                    .filter(d -> d.getDiscountPercentage() == percentage)
                                    .toList();
                        } catch (NumberFormatException e) {
                            // If input is not a valid number, show all discounts
                            filteredDiscounts = discountService.showAll();
                        }
                    }
                    loadPromotions(filteredDiscounts);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
        addButton.setOnMouseEntered(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseExited(event -> addButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff791f; -fx-background-radius: 4;"));
        addButton.setOnMousePressed(event -> addButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseReleased(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));

    }

    private void loadPromotions(List<Discount> discounts) {
        for (Discount discount : discounts) {
            VBox discountBox = createDiscountCard(discount);
            discountContainer.getChildren().add(discountBox);
        }
    }

    private VBox createDiscountCard(Discount discount) {
        VBox discountBox = new VBox();
        discountBox.setPrefWidth(300);
        discountBox.setMaxWidth(250);
        discountBox.setMinHeight(100);
        discountBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 15px; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

        // Discount header
        HBox headerContainer = new HBox();
        headerContainer.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox();
        header.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-border-radius: 15px 15px 0 0; " +
                "-fx-background-radius: 15px 0 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");
        header.setPrefWidth(180);

        Label idLabel = new Label("Promotion: " + discount.getDiscountPercentage() +"%");
        idLabel.setStyle(" -fx-text-fill: #000000; -fx-font-size: 15px;");
        header.getChildren().add(idLabel);

        // Icons container
        HBox iconsBox = new HBox();
        iconsBox.setAlignment(Pos.CENTER_RIGHT);
        iconsBox.setSpacing(5);
        iconsBox.setPadding(new Insets(0, 10, 0, 0));
        iconsBox.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-border-radius: 0 15px 0 0; " +
                "-fx-background-radius: 0 15px 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");

        // --------------Edit icon
        Button editButton = new Button();

              ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/edit.png")));
              editIcon.setFitHeight(13);
              editIcon.setFitWidth(13);
              editButton.setGraphic(editIcon);
              editButton.setStyle("-fx-background-color: transparent;");
              editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82);"));
              editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: transparent;"));
        // --------------Delete icon
        Button deleteButton = new Button();

              ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/trash.png")));
              deleteIcon.setFitHeight(13);
              deleteIcon.setFitWidth(13);
              deleteButton.setGraphic(deleteIcon);
              deleteButton.setStyle("-fx-background-color: transparent;");
              deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82);"));
              deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: transparent;"));

        //putting both icons in the container
        iconsBox.getChildren().addAll(editButton, deleteButton);

        // header+ container icons
        headerContainer.getChildren().addAll(header, iconsBox);
        HBox.setHgrow(iconsBox, Priority.ALWAYS);

        // Discount details
        VBox details = new VBox();
        details.setStyle("-fx-padding: 30;");
        Label startDate = new Label("Commence en: " + discount.getStartDate());
        startDate.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 14px;");
        Label endDate = new Label("S'expire en: " + discount.getEndDate());
        endDate.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 14px;");
        details.getChildren().addAll(startDate, endDate);

        //collect all s"ection in the card
        discountBox.getChildren().addAll(headerContainer, details);
        // event handlers for edit and delete buttons
        editButton.setOnAction(e -> showEditDialog(discount));
        deleteButton.setOnAction(e -> showDeleteConfirmation(discount));

        return discountBox;
    }

    private void showAddPromotionPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutPromotion.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ajouter une promotion");
            popupStage.setScene(new Scene(root));

            // display popup and wait for it to close
            popupStage.showAndWait();

            try {
                List<Discount> discounts = discountService.showAll();
                discountContainer.getChildren().clear();
                loadPromotions(discounts);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }

    //-------------------------------------------------------
    private void showEditDialog(Discount discount) {
        if (discount == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune promotion sélectionnée.");
            return;
        }

        try {
            // Create a copy of the discount to edit
            Discount discountCopy = new Discount();
            discountCopy.setId(discount.getId());
            discountCopy.setShopId(discount.getShopId());
            discountCopy.setDiscountPercentage(discount.getDiscountPercentage());
            discountCopy.setStartDate(discount.getStartDate());
            discountCopy.setEndDate(discount.getEndDate());

            // Load the FXML page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModificationPromotion.fxml"));
            Parent root = loader.load();
            // Set the discount copy in the controller
            ModificationPromotionController controller = loader.getController();
            controller.setDiscount(discountCopy);

            // Create and configure the popup stage
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Modifier une promotion");
            popupStage.setScene(new Scene(root));

            // Show the popup and wait for it to close
            popupStage.showAndWait();

            // Reload promotions after the popup is closed
            try {
                List<Discount> discounts = discountService.showAll();
                discountContainer.getChildren().clear();
                loadPromotions(discounts);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour la promotion: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'édition: " + e.getMessage());
        }
    }


    // Method to show delete confirmation
    private void showDeleteConfirmation(Discount discount) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette promotion?");
        alert.setContentText("Cette action ne peut pas être annulée.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int deleted = deleteDiscount(discount);
                if (deleted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "La promotion a été supprimée avec succès.");
                    List<Discount> discounts = discountService.showAll();
                    discountContainer.getChildren().clear();
                    loadPromotions(discounts);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la promotion.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données: " + ex.getMessage());
            }
        }
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

    // Method to call the discount delete service
    private int deleteDiscount(Discount discount) throws SQLException {
        // Assuming you have a service or DAO instance
        DiscountService discountService = new DiscountService();
        return discountService.delete(discount);
    }

}