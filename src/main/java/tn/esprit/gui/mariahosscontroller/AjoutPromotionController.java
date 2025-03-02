package tn.esprit.gui.mariahosscontroller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Discount;
import tn.esprit.listeners.EmailNotificationListener;
import tn.esprit.services.mariahossservice.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import tn.esprit.utils.Session;

public class AjoutPromotionController {
    @FXML
    private TextField percentageField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final DiscountService discountService = new DiscountService();
    private Session session = Session.getInstance();

    @FXML
    public void initialize() {
        // Set default dates
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

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(30)); // Default to 30 days promotion

        saveButton.setOnAction(event -> savePromotion());
        cancelButton.setOnAction(event -> closePopup());
    }

    private void savePromotion() {
        try {
            // Validate percentage
            float percentage;
            try {
                percentage = Float.parseFloat(percentageField.getText());
                if (percentage <= 0 || percentage > 100) {
                    showAlert("Le pourcentage doit être entre 0 et 100.", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Format de pourcentage invalide. Veuillez entrer un nombre décimal.", Alert.AlertType.ERROR);
                return;
            }

            // Validate dates
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                showAlert("Les dates de début et de fin sont requises.", Alert.AlertType.ERROR);
                return;
            }

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate.isAfter(endDate)) {
                showAlert("La date de début ne peut pas être après la date de fin.", Alert.AlertType.ERROR);
                return;
            }

            // Convert LocalDate to java.util.Date
            Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create discount object
            Discount discount = new Discount();
            discount.setShopId(session.getCurrentUser().getId());
            discount.setDiscountPercentage(percentage);
            discount.setStartDate(start);
            discount.setEndDate(end);

            // Add email notification listener
            List<String> userEmails = Arrays.asList("ammarim073@gmail.com"); // here is the emails of users
            discountService.addListener(new EmailNotificationListener(userEmails));

            // Save the discount
            int result = discountService.insert(discount);

            if (result > 0) {
                showAlert("Promotion ajoutée avec succès!", Alert.AlertType.INFORMATION);
                closePopup();
            } else {
                showAlert("Échec de l'ajout de la promotion.", Alert.AlertType.ERROR);
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