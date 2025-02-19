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
import tn.esprit.entities.Discount;
import tn.esprit.services.DiscountService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
            List<Discount> discounts = discountService.showAll();
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
        discountBox.setPrefWidth(250);
        discountBox.setMaxWidth(250);
        discountBox.setMinHeight(100);
        discountBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 15px; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        // Discount header
        VBox header = new VBox();
        header.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-border-radius: 15px 15px 0 0; " +
                "-fx-background-radius: 15px 15px 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");
        Label idLabel = new Label("Pourcentage de promotion: \n" + discount.getDiscountPercentage() +"%");
        idLabel.setStyle(" -fx-text-fill: #000000; -fx-font-size: 15px;");
        header.getChildren().add(idLabel);

        // Discount details
        VBox details = new VBox();
        details.setStyle("-fx-padding: 30;");
        Label startDate = new Label("Commence en: " + discount.getStartDate());
        startDate.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 14px;");
        Label endDate = new Label("S'expire en: " + discount.getEndDate());
        endDate.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 14px;");
        details.getChildren().addAll(startDate, endDate);

        // Add sections to card
        discountBox.getChildren().addAll(header, details);

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
}