package tn.esprit.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class ReservationDialog {

    public static Optional<LocalDateTime> showReservationDialog() {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Parking Reservation");

        try {
            // Load the FXML and CSS files
            URL fxmlUrl = ReservationDialog.class.getResource("/fxml/reservation_dialog.fxml");
            URL cssUrl = ReservationDialog.class.getResource("/css/style.css");
            if (fxmlUrl == null || cssUrl == null) {
                throw new RuntimeException("FXML or CSS file not found.");
            }

            // Load content and style
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            VBox content = loader.load();
            content.getStylesheets().add(cssUrl.toExternalForm());

            // Get references to components
            DatePicker datePicker = (DatePicker) content.lookup("#datePicker");
            Spinner<Integer> hourSpinner = (Spinner<Integer>) content.lookup("#hourSpinner");
            Spinner<Integer> minuteSpinner = (Spinner<Integer>) content.lookup("#minuteSpinner");
            Label errorLabel = (Label) content.lookup("#errorLabel");

            // Set default date
            datePicker.setValue(LocalDate.now());

            // Format spinners to always show two digits
            formatTimeSpinner(hourSpinner);
            formatTimeSpinner(minuteSpinner);

            // Configure dialog
            dialog.getDialogPane().setContent(content);
            ButtonType confirmButton = new ButtonType("Confirm Reservation", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

            // Enable/disable confirm button based on validation
            Button confirmButtonNode = (Button) dialog.getDialogPane().lookupButton(confirmButton);
            confirmButtonNode.setDisable(true);

            // Add validation listeners
            addValidationListeners(datePicker, hourSpinner, minuteSpinner, errorLabel, confirmButtonNode);

            // Set result converter
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButton) {
                    return LocalDateTime.of(
                            datePicker.getValue(),
                            LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue())
                    );
                }
                return null;
            });

            // Style the dialog
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.setMinWidth(400);
            stage.setMinHeight(300);

            return dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load reservation dialog: " + e.getMessage(), ButtonType.OK).showAndWait();
            return Optional.empty();
        }
    }

    private static void formatTimeSpinner(Spinner<Integer> spinner) {
        spinner.setEditable(true);
        SpinnerValueFactory<Integer> factory = spinner.getValueFactory();
        if (factory instanceof IntegerSpinnerValueFactory) {
            factory.setConverter(new javafx.util.StringConverter<Integer>() {
                @Override
                public String toString(Integer value) {
                    return String.format("%02d", value);
                }

                @Override
                public Integer fromString(String string) {
                    try {
                        return Integer.parseInt(string);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            });
        }
    }

    private static void addValidationListeners(DatePicker datePicker, Spinner<Integer> hourSpinner,
                                               Spinner<Integer> minuteSpinner, Label errorLabel, Button confirmButton) {
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateInput(datePicker, hourSpinner, minuteSpinner, errorLabel, confirmButton));
        hourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateInput(datePicker, hourSpinner, minuteSpinner, errorLabel, confirmButton));
        minuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateInput(datePicker, hourSpinner, minuteSpinner, errorLabel, confirmButton));
    }

    private static void validateInput(DatePicker datePicker, Spinner<Integer> hourSpinner,
                                      Spinner<Integer> minuteSpinner, Label errorLabel, Button confirmButton) {
        LocalDateTime selectedDateTime = LocalDateTime.of(
                datePicker.getValue(),
                LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue())
        );

        if (selectedDateTime.isBefore(LocalDateTime.now())) {
            errorLabel.setText("Please select a future date and time");
            errorLabel.setVisible(true);
            confirmButton.setDisable(true);
        } else {
            errorLabel.setVisible(false);
            confirmButton.setDisable(false);
        }
    }
}
