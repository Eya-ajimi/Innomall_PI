package tn.esprit.gui.sofienecontroller;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReservationDialog {

    // Pricing constants
    private static final double BASE_HOURLY_RATE = 1.0;
    private static final Map<String, Double> VEHICLE_TYPE_MULTIPLIERS = new HashMap<String, Double>() {{
        put("Motorcycle", 3.5);
        put("Compact", 5.0);
        put("SUV", 6.0);
        put("Van", 7.5);
    }};
    private static final Map<String, Double> CAR_WASH_PRICES = new HashMap<String, Double>() {{
        put("Basic", 10.0);
        put("Premium", 20.0);
        put("Deluxe", 30.0);
    }};
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
            URL cssUrl = ReservationDialog.class.getResource("/css/reservation_dialog_css.css");
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
            CheckBox carWashCheckBox = (CheckBox) content.lookup("#carWashCheckBox");
            ComboBox<String> carWashTypeCombo = (ComboBox<String>) content.lookup("#carWashTypeCombo");
            TextArea notesArea = (TextArea) content.lookup("#notesArea");
            Label durationLabel = (Label) content.lookup("#durationLabel");
            Label parkingPriceLabel = (Label) content.lookup("#parkingPriceLabel");
            Label carWashPriceLabel = (Label) content.lookup("#carWashPriceLabel");
            Label totalPriceLabel = (Label) content.lookup("#totalPriceLabel");
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
                    endHourSpinner, endMinuteSpinner, vehicleTypeCombo,
                    carWashCheckBox, carWashTypeCombo);

            // Add validation and update listeners
            addListeners(startDatePicker, endDatePicker, startHourSpinner, startMinuteSpinner,
                    endHourSpinner, endMinuteSpinner, vehicleTypeCombo,
                    carWashCheckBox, carWashTypeCombo, errorLabel, confirmButton,
                    durationLabel, parkingPriceLabel, carWashPriceLabel, totalPriceLabel);

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

                    String carWashType = null;
                    if (carWashCheckBox.isSelected()) {
                        carWashType = carWashTypeCombo.getValue();
                    }

                    return new ReservationDetails(
                            startDateTime,
                            endDateTime,
                            vehicleTypeCombo.getValue(),
                            carWashType,
                            notesArea.getText()
                    );
                }
                return null;
            });

            // Configure dialog window
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.setMinWidth(550);
            stage.setMinHeight(700);

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
            ComboBox<String> vehicleTypeCombo,
            CheckBox carWashCheckBox,
            ComboBox<String> carWashTypeCombo) {

        // Set default dates
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today);
        endDatePicker.setValue(today);

        // Configure time spinners
        configureTimeSpinner(startHourSpinner, 0, 23, LocalTime.now().getHour());
        configureTimeSpinner(startMinuteSpinner, 0, 59, roundToNearestFive(LocalTime.now().getMinute()));
        configureTimeSpinner(endHourSpinner, 0, 23, LocalTime.now().plusHours(1).getHour());
        configureTimeSpinner(endMinuteSpinner, 0, 59, roundToNearestFive(LocalTime.now().getMinute()));

        // Set up vehicle types
        vehicleTypeCombo.getItems().addAll(
                "Motorcycle",
                "Compact",
                "SUV",
                "Van"
        );
        vehicleTypeCombo.setValue("Compact"); // Default value

        // Set up car wash options
        carWashTypeCombo.getItems().addAll(
                "Basic - 10 TND",
                "Premium - 20 TND",
                "Deluxe - 30 TND"
        );
        carWashTypeCombo.setValue("Basic - 10 TND");
        carWashTypeCombo.setDisable(true);

        // Connect car wash checkbox with combo box
        carWashCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            carWashTypeCombo.setDisable(!newVal);
        });
    }

    private static int roundToNearestFive(int minute) {
        return ((minute + 2) / 5) * 5 % 60;
    }

    private static void configureTimeSpinner(Spinner<Integer> spinner, int min, int max, int initialValue) {
        spinner.setValueFactory(new IntegerSpinnerValueFactory(min, max, initialValue));
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

    private static void addListeners(
            DatePicker startDatePicker,
            DatePicker endDatePicker,
            Spinner<Integer> startHourSpinner,
            Spinner<Integer> startMinuteSpinner,
            Spinner<Integer> endHourSpinner,
            Spinner<Integer> endMinuteSpinner,
            ComboBox<String> vehicleTypeCombo,
            CheckBox carWashCheckBox,
            ComboBox<String> carWashTypeCombo,
            Label errorLabel,
            Button confirmButton,
            Label durationLabel,
            Label parkingPriceLabel,
            Label carWashPriceLabel,
            Label totalPriceLabel) {

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
                    parkingPriceLabel.setText("--");
                    carWashPriceLabel.setText("--");
                    totalPriceLabel.setText("--");
                    return;
                }

                // Calculate duration
                long hours = ChronoUnit.HOURS.between(startDateTime, endDateTime);
                long minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime) % 60;
                durationLabel.setText(String.format("%d hours %d minutes", hours, minutes));

                // Calculate parking price
                double totalHours = hours + (minutes / 60.0);
                double vehicleRate = VEHICLE_TYPE_MULTIPLIERS.getOrDefault(vehicleTypeCombo.getValue(), 5.0);
                double parkingPrice = totalHours * BASE_HOURLY_RATE * vehicleRate;
                parkingPriceLabel.setText(String.format("%.2f TND", parkingPrice));

                // Calculate car wash price
                double carWashPrice = 0.0;
                if (carWashCheckBox.isSelected() && carWashTypeCombo.getValue() != null) {
                    carWashPrice = getCarWashPrice(carWashTypeCombo.getValue());
                    carWashPriceLabel.setText(String.format("%.2f TND", carWashPrice));
                } else {
                    carWashPriceLabel.setText("N/A");
                }

                // Calculate total price
                double totalPrice = parkingPrice + carWashPrice;
                totalPriceLabel.setText(String.format("%.2f TND", totalPrice));

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
        carWashCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());
        carWashTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateAndUpdate.run());

        // Initial validation
        validateAndUpdate.run();
    }

    private static double getCarWashPrice(String carWashType) {
        if (carWashType == null) return 0.0;

        if (carWashType.contains("Basic")) return CAR_WASH_PRICES.get("Basic");
        if (carWashType.contains("Premium")) return CAR_WASH_PRICES.get("Premium");
        if (carWashType.contains("Deluxe")) return CAR_WASH_PRICES.get("Deluxe");

        return 0.0;
    }

    private static String validateReservation(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String vehicleType) {

        if (startDateTime.isBefore(LocalDateTime.now())) {
            return "Start time must be in the future";
        }
        if (endDateTime.isBefore(startDateTime)) {
            return "End time must be after start time";
        }
        if (endDateTime.isBefore(LocalDateTime.now())) {
            return "End time must be in the future";
        }
        if (ChronoUnit.HOURS.between(startDateTime, endDateTime) > 72) {
            return "Maximum reservation duration is 72 hours";
        }
        if (vehicleType == null || vehicleType.isEmpty()) {
            return "Please select a vehicle type";
        }

        // Additional validation could be added here

        return null;
    }

    public static class ReservationDetails {
        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;
        private final String vehicleType;
        private final String carWashType;
        private final String notes;

        public ReservationDetails(
                LocalDateTime startDateTime,
                LocalDateTime endDateTime,
                String vehicleType,
                String carWashType,
                String notes) {
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.vehicleType = vehicleType;
            this.carWashType = carWashType;
            this.notes = notes;
        }

        public LocalDateTime getStartDateTime() { return startDateTime; }
        public LocalDateTime getEndDateTime() { return endDateTime; }
        public String getVehicleType() { return vehicleType; }
        public String getCarWashType() { return carWashType; }
        public boolean hasCarWash() { return carWashType != null && !carWashType.isEmpty(); }
        public String getNotes() { return notes; }

        public double getTotalPrice() {
            long hours = ChronoUnit.HOURS.between(startDateTime, endDateTime);
            long minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime) % 60;
            double totalHours = hours + (minutes / 60.0);
            double vehicleMultiplier = VEHICLE_TYPE_MULTIPLIERS.getOrDefault(vehicleType, 1.0);
            double parkingPrice = totalHours * BASE_HOURLY_RATE * vehicleMultiplier;

            double carWashPrice = hasCarWash() ? getCarWashPrice(carWashType) : 0.0;

            return parkingPrice + carWashPrice;
        }
    }
}