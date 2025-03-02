package tn.esprit.gui.mariahosscontroller;
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
import tn.esprit.services.mariahossservice.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import tn.esprit.utils.Session;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PromotionController {

    @FXML
    private FlowPane discountContainer;

    @FXML
    private TextField searchField;

    @FXML
    private Button addButton;

    private final DiscountService discountService = new DiscountService();
    private Session session = Session.getInstance();

    @FXML
    public void initialize() {
        try {
            List<Discount> discounts = discountService.getDiscountByShopId(session.getCurrentUser().getId()); // Use current user's ID
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
                "-fx-border-color: #e5e5e5; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");

        // Discount header with gradient background
        HBox headerContainer = new HBox();
        headerContainer.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox();
        // Use color that matches the orange theme from the main UI
        header.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-border-radius: 12px 0 0 0; " +
                "-fx-background-radius: 12px 0 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #f0f0f0; " +
                "-fx-border-width: 0 0 1 0;");
        header.setPrefWidth(180);

        // Create a more prominent discount display
        Label discountLabel = new Label(discount.getDiscountPercentage() + "%");
        discountLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label promotionLabel = new Label("PROMOTION");
        promotionLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px; -fx-font-weight: bold;");

        header.getChildren().addAll(promotionLabel, discountLabel);

        // Icons container with matching style
        HBox iconsBox = new HBox();
        iconsBox.setAlignment(Pos.CENTER_RIGHT);
        iconsBox.setSpacing(8);
        iconsBox.setPadding(new Insets(0, 15, 0, 0));
        iconsBox.setStyle("-fx-background-color:  rgba(255, 165, 0, 0.50);" +
                "-fx-border-radius: 0 12px 0 0; " +
                "-fx-background-radius: 0 12px 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #f0f0f0; " +
                "-fx-border-width: 0 0 1 0;");

        // Edit icon with improved hover effect
        Button editButton = new Button();
        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/edit.png")));
        editIcon.setFitHeight(16);
        editIcon.setFitWidth(16);
        editButton.setGraphic(editIcon);
        editButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-background-radius: 4px; -fx-padding: 5 8;");
        editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.6); -fx-background-radius: 4px; -fx-padding: 5 8;"));
        editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-background-radius: 4px; -fx-padding: 5 8;"));

        // Delete icon with improved hover effect
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/trash.png")));
        deleteIcon.setFitHeight(16);
        deleteIcon.setFitWidth(16);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-background-radius: 4px; -fx-padding: 5 8;");
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: rgb(255,70,70); -fx-background-radius: 4px; -fx-padding: 5 8;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-background-radius: 4px; -fx-padding: 5 8;"));

        // Add icons to container
        iconsBox.getChildren().addAll(editButton, deleteButton);

        // Combine header and icons
        headerContainer.getChildren().addAll(header, iconsBox);
        HBox.setHgrow(iconsBox, Priority.ALWAYS);

        // Discount details section with improved styling
        VBox details = new VBox();
        details.setSpacing(12);
        details.setStyle("-fx-padding: 15 20;");

        // Create date labels with icons
        HBox startDateBox = new HBox(10);
        startDateBox.setAlignment(Pos.CENTER_LEFT);
        Label startDateIcon = new Label("📅");
        Label startDateLabel = new Label("Commence: " + formatDate(discount.getStartDate()));
        startDateLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");
        startDateBox.getChildren().addAll(startDateIcon, startDateLabel);

        HBox endDateBox = new HBox(10);
        endDateBox.setAlignment(Pos.CENTER_LEFT);
        Label endDateIcon = new Label("⏰");
        Label endDateLabel = new Label("Expire: " + formatDate(discount.getEndDate()));
        endDateLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");
        endDateBox.getChildren().addAll(endDateIcon, endDateLabel);

        // Add status indicator
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        String statusText;
        String statusColor;

        // Determine if promotion is active, upcoming, or expired
        Date now = new Date();
        Date startDate = discount.getStartDate();
        Date endDate = discount.getEndDate();

        if (now.after(endDate)) {
            statusText = "Expiré";
            statusColor = "#ff4545";
        } else if (now.before(startDate)) {
            statusText = "À venir";
            statusColor = "#4da6ff";
        } else {
            statusText = "Actif";
            statusColor = "#4caf50";
        }

        Label statusIndicator = new Label("●");
        statusIndicator.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 16px;");

        Label statusLabel = new Label(statusText);
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-weight: bold; -fx-font-size: 14px;");

        statusBox.getChildren().addAll(statusIndicator, statusLabel);

        // Add all elements to details section
        details.getChildren().addAll(statusBox, startDateBox, endDateBox);

        // Collect all sections in the card
        discountBox.getChildren().addAll(headerContainer, details);

        // Event handlers for edit and delete buttons
        editButton.setOnAction(e -> showEditDialog(discount));
        deleteButton.setOnAction(e -> showDeleteConfirmation(discount));

        return discountBox;
    }

    // Helper method to format dates nicely using java.util.Date
    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        return formatter.format(date);
    }

    private void showAddPromotionPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AjoutPromotion.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModificationPromotion.fxml"));
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