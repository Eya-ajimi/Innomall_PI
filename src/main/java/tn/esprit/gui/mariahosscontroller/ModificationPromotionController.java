package tn.esprit.gui.mariahosscontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Discount;
import tn.esprit.services.mariahossservice.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class ModificationPromotionController {

    // FXML fields
    @FXML
    private TextField percentageField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    // The discount to edit
    private Discount discount;

    // State of the popup
    private boolean saveClicked = false;

    // Initialize the discount object in the controller
    public void setDiscount(Discount copy_discount) {
        if (copy_discount == null) {
            throw new IllegalArgumentException("Discount cannot be null");
        }
        this.discount = copy_discount;

        // Set percentage field
        percentageField.setText(String.valueOf(discount.getDiscountPercentage()));

        // Convert java.util.Date to java.sql.Date (if necessary) and then to LocalDate
        if (discount.getStartDate() != null) {
            LocalDate startLocalDate = ((java.sql.Date) discount.getStartDate()).toLocalDate();
            startDatePicker.setValue(startLocalDate);
        } else {
            startDatePicker.setValue(null);
        }

        if (discount.getEndDate() != null) {
            LocalDate endLocalDate = ((java.sql.Date) discount.getEndDate()).toLocalDate();
            endDatePicker.setValue(endLocalDate);
        } else {
            endDatePicker.setValue(null);
        }
    }

    // Get the discount
    public Discount getDiscount() {
        return discount;
    }

    // Initialize the controller
    @FXML
    private void initialize() {
        //cancel
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #d6d6d6; -fx-text-fill: #333333; -fx-background-radius: 4;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-background-radius: 4;"));
        cancelButton.setOnMousePressed(e -> cancelButton.setStyle("-fx-background-color: #bfbfbf; -fx-text-fill: #333333; -fx-background-radius: 4;"));
        cancelButton.setOnMouseReleased(e -> cancelButton.setStyle("-fx-background-color: #d6d6d6; -fx-text-fill: #333333; -fx-background-radius: 4;"));

        // Save
        saveButton.setOnMouseEntered(e -> saveButton.setStyle("-fx-background-color: #ff8c33; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
        saveButton.setOnMouseExited(e -> saveButton.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82); -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
        saveButton.setOnMousePressed(e -> saveButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));
        saveButton.setOnMouseReleased(e -> saveButton.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82); -fx-text-fill: white; -fx-background-radius: 4; -fx-font-weight: bold;"));

        // Ensure FXML fields are properly initialized
        if (percentageField == null || startDatePicker == null || endDatePicker == null || saveButton == null || cancelButton == null) {
            throw new IllegalStateException("FXML fields are not properly initialized");
        }

        // Add validation listeners
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());

        // Only allow numeric input for percentage
        percentageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                percentageField.setText(oldValue);
            }
        });

        // Date validation: end date must be after start date
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && startDatePicker.getValue() != null && newValue.isBefore(startDatePicker.getValue())) {
                // Show error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Date Invalide");
                alert.setHeaderText("Date de fin invalide");
                alert.setContentText("La date de fin doit être après la date de début");
                alert.showAndWait();

                // Reset to old value
                endDatePicker.setValue(oldValue);
            }
        });
    }
    @FXML
    private void handleSave() {
        if (discount == null) {
            showErrorAlert("Erreur", "Aucune promotion à mettre à jour", "Veuillez sélectionner une promotion valide.");
            return;
        }

        if (isInputValid()) {
            try {
                // Update discount object with new values
                if (percentageField.getText() != null && !percentageField.getText().isEmpty()) {
                    try {
                        discount.setDiscountPercentage(Float.parseFloat(percentageField.getText()));
                    } catch (NumberFormatException e) {
                        showErrorAlert("Erreur", "Format invalide", "Veuillez saisir un pourcentage valide.");
                        return;
                    }
                }

                // Convert LocalDate to java.sql.Date
                if (startDatePicker.getValue() != null) {
                    discount.setStartDate(java.sql.Date.valueOf(startDatePicker.getValue()));
                }

                if (endDatePicker.getValue() != null) {
                    discount.setEndDate(java.sql.Date.valueOf(endDatePicker.getValue()));
                }

                DiscountService discountService = new DiscountService();
                int result = discountService.update(discount);

                if (result > 0) {
                    showInfoAlert("Succès", "Mise à jour réussie", "La promotion a été mise à jour avec succès.");
                    closePopup();
                } else {
                    showErrorAlert("Erreur", "Échec de la mise à jour", "Aucune modification apportée.");
                }
            } catch (SQLException e) {
                showErrorAlert("Erreur SQL", "Une erreur est survenue", "Détails : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void closePopup() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    // Handle cancel button click
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Check if save was clicked
    public boolean isSaveClicked() {
        return saveClicked;
    }

    // Set the save clicked flag
    public void setSaveClicked(boolean value) {
        this.saveClicked = value;
    }

    // Validate input fields
    private boolean isInputValid() {
        String errorMessage = "";

        // Validate percentage
        if (percentageField.getText() == null || percentageField.getText().isEmpty()) {
            errorMessage += "Pourcentage de promotion non valide!\n";
        } else {
            try {
                float percentage = Float.parseFloat(percentageField.getText());
                if (percentage <= 0 || percentage > 100) {
                    errorMessage += "Pourcentage doit être entre 0 et 100!\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += "Pourcentage doit être un nombre!\n";
            }
        }

        // Validate start date
        if (startDatePicker.getValue() == null) {
            errorMessage += "Date de début non valide!\n";
        }

        // Validate end date
        if (endDatePicker.getValue() == null) {
            errorMessage += "Date de fin non valide!\n";
        } else if (startDatePicker.getValue() != null &&
                endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            errorMessage += "La date de fin doit être après la date de début!\n";
        }

        // Check if end date is in the future
        if (endDatePicker.getValue() != null &&
                endDatePicker.getValue().isBefore(LocalDate.now())) {
            errorMessage += "La date de fin ne peut pas être dans le passé!\n";
        }

        // Show error message if any
        if (!errorMessage.isEmpty()) {
            showErrorAlert("Champs Invalides", "Veuillez corriger les champs invalides", errorMessage);
            return false;
        }

        return true;
    }

    // Helper method to show error alerts
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Helper method to show info alerts
    private void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}