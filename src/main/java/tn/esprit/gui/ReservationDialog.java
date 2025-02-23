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
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ReservationDialog {

    // Original method for backward compatibility
    public static Optional<LocalDateTime> showReservationDialog() {
        return showEnhancedReservationDialog().map(ReservationDetails::getStartDateTime);
    }

    // Enhanced method that returns all reservation details
    public static Optional<ReservationDetails> showEnhancedReservationDialog() {
        Dialog<ReservationDetails> dialog = new Dialog<>();
        dialog.setTitle("Parking Reservation");
        dialog.setHeaderText("Book Your Parking Spot");

        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(ReservationDialog.class.getResource("/fxml/reservation_dialog.fxml"));
            VBox content = loader.load();

            // Load CSS
            URL cssUrl = ReservationDialog.class.getResource("/css/style.css");
            if (cssUrl != null) {
                content.getStylesheets().add(cssUrl.toExternalForm());
            }

            // Get all UI components
            DatePicker startDatePicker = (DatePicker) content.lookup("#startDatePicker");
            Spinner<Integer> startHourSpinner = (Spinner<Integer>) content.lookup("#startHourSpinner");
            Spinner<Integer> startMinuteSpinner = (Spinner<Integer>) content.lookup("#startMinuteSpinner");
            DatePicker endDatePicker = (DatePicker) content.lookup("#endDatePicker");
            Spinner<Integer> endHourSpinner = (Spinner<Integer>) content.lookup("#endHourSpinner");
            Spinner<Integer> endMinuteSpinner = (Spinner<Integer>) content.lookup("#endMinuteSpinner");
            ComboBox<String> vehicleTypeCombo = (ComboBox<String>) content.lookup("#vehicleTypeCombo");
            TextArea notesArea = (TextArea) content.lookup("#notesArea");
            Label durationLabel = (Label) content.lookup("#durationLabel");
            Label priceLabel = (Label) content.lookup("#priceLabel");
            Label errorLabel = (Label) content.lookup("#errorLabel");

            // Set up the dialog
            dialog.getDialogPane().setContent(content);
            ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            // Get the confirm button
            Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
            confirmButton.setDisable(true);

            // Initialize all components
            initializeComponents(startDatePicker, endDatePicker, startHourSpinner, startMinuteSpinner,
                    endHourSpinner, endMinuteSpinner, vehicleTypeCombo);

            // Add validation listeners
            addValidationListeners(startDatePicker, endDatePicker, startHourSpinner, startMinuteSpinner,
                    endHourSpinner, endMinuteSpinner, vehicleTypeCombo, errorLabel, confirmButton,
                    durationLabel, priceLabel);

            // Set up result converter
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    LocalDateTime startDateTime = LocalDateTime.of(
                            startDatePicker.getValue(),
                            LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue())
                    );
                    LocalDateTime endDateTime = LocalDateTime.of(
                            endDatePicker.getValue(),
                            LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue())
                    );
                    return new ReservationDetails(
                            startDateTime,
                            endDateTime,
                            vehicleTypeCombo.getValue(),
                            notesArea.getText()
                    );
                }
                return null;
            });

            // Configure dialog window
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.setMinWidth(500);
            stage.setMinHeight(600);

            // Show dialog and return result
            return dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to load reservation dialog: " + e.getMessage(),
                    ButtonType.OK).showAndWait();
            return Optional.empty();
        }
    }

    private static void initializeComponents(
            DatePicker startDatePicker,
            DatePicker endDatePicker,
            Spinner<Integer> startHourSpinner,
            Spinner<Integer> startMinuteSpinner,
            Spinner<Integer> endHourSpinner,
            Spinner<Integer> endMinuteSpinner,
            ComboBox<String> vehicleTypeCombo) {

        // Set default dates
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today);
        endDatePicker.setValue(today);

        // Configure time spinners
        formatTimeSpinner(startHourSpinner);
        formatTimeSpinner(startMinuteSpinner);
        formatTimeSpinner(endHourSpinner);
        formatTimeSpinner(endMinuteSpinner);

        // Set up vehicle types
        vehicleTypeCombo.getItems().addAll(
                "Sedan",
                "SUV",
                "Van",
                "Motorcycle",
                "Electric Vehicle"
        );
        vehicleTypeCombo.setValue("Sedan"); // Default value
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

    private static void addValidationListeners(
            DatePicker startDatePicker,
            DatePicker endDatePicker,
            Spinner<Integer> startHourSpinner,
            Spinner<Integer> startMinuteSpinner,
            Spinner<Integer> endHourSpinner,
            Spinner<Integer> endMinuteSpinner,
            ComboBox<String> vehicleTypeCombo,
            Label errorLabel,
            Button confirmButton,
            Label durationLabel,
            Label priceLabel) {

        Runnable validateAndUpdate = () -> {
            try {
                LocalDateTime startDateTime = LocalDateTime.of(
                        startDatePicker.getValue(),
                        LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue())
                );
                LocalDateTime endDateTime = LocalDateTime.of(
                        endDatePicker.getValue(),
                        LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue())
                );

                String error = validateReservation(startDateTime, endDateTime, vehicleTypeCombo.getValue());
                if (error != null) {
                    errorLabel.setText(error);
                    errorLabel.setVisible(true);
                    confirmButton.setDisable(true);
                    durationLabel.setText("Invalid duration");
                    priceLabel.setText("--");
                    return;
                }

                // Calculate duration
                long hours = ChronoUnit.HOURS.between(startDateTime, endDateTime);
                long minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime) % 60;
                durationLabel.setText(String.format("%d hours %d minutes", hours, minutes));

                // Calculate price (example: $2 per hour)
                double totalHours = hours + (minutes / 60.0);
                double price = totalHours * 2.0;
                priceLabel.setText(String.format("$%.2f", price));

                errorLabel.setVisible(false);
                confirmButton.setDisable(false);

            } catch (Exception e) {
                errorLabel.setText("Invalid date/time selection");
                errorLabel.setVisible(true);
                confirmButton.setDisable(true);
            }
        };

        // Add listeners to all components
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());
        startHourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());
        startMinuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());
        endHourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());
        endMinuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());
        vehicleTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());
    }

    private static String validateReservation(LocalDateTime startDateTime, LocalDateTime endDateTime, String vehicleType) {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            return "Start time must be in the future";
        }
        if (endDateTime.isBefore(startDateTime)) {
            return "End time must be after start time";
        }
        if (endDateTime.isBefore(LocalDateTime.now())) {
            return "End time must be in the future";
        }
        if (ChronoUnit.HOURS.between(startDateTime, endDateTime) > 24) {
            return "Maximum reservation duration is 24 hours";
        }
        if (vehicleType == null || vehicleType.isEmpty()) {
            return "Please select a vehicle type";
        }
        return null;
    }

    public static class ReservationDetails {
        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;
        private final String vehicleType;
        private final String notes;

        public ReservationDetails(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                  String vehicleType, String notes) {
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.vehicleType = vehicleType;
            this.notes = notes;
        }

        public LocalDateTime getStartDateTime() { return startDateTime; }
        public LocalDateTime getEndDateTime() { return endDateTime; }
        public String getVehicleType() { return vehicleType; }
        public String getNotes() { return notes; }
    }
}