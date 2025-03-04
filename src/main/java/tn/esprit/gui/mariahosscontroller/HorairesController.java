package tn.esprit.gui.mariahosscontroller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import tn.esprit.entities.Schedule;
import tn.esprit.entities.enums.Days;
import tn.esprit.services.mariahossservice.*;

import java.net.URL;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class HorairesController implements Initializable {

    @FXML
    private FlowPane scheduleContainer;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> dayFilter;

    @FXML
    private Button refreshButton;

    // Service for database operations
    private ScheduleService scheduleService;

    // Current displayed schedules
    private List<Schedule> schedules;

    private int currentshopid=1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the schedule service
        scheduleService = new ScheduleService();
        setupDayFilter();
        setupSearch();
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshScheduleDisplay());
        }

        // Initial data load
        loadSchedules();
    }

    private void setupDayFilter() {
        if (dayFilter != null) {
            // Add days to the filter
            dayFilter.getItems().add("Tous les jours");
            dayFilter.getItems().add("Lundi");
            dayFilter.getItems().add("Mardi");
            dayFilter.getItems().add("Mercredi");
            dayFilter.getItems().add("Jeudi");
            dayFilter.getItems().add("Vendredi");
            dayFilter.getItems().add("Samedi");
            dayFilter.getItems().add("Dimanche");

            // Set default selection
            dayFilter.setValue("Tous les jours");

            // Add filter action
            dayFilter.setOnAction(e -> filterSchedulesByDay(dayFilter.getValue()));
        }
    }

    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterSchedules(newValue);
            });
        }
    }

    private void loadSchedules() {
        try {
            // Get all schedules from service
            schedules = scheduleService.getScheduleByShopOwnerId(currentshopid);
            displaySchedules(schedules);
        } catch (Exception e) {
            showAlert("Erreur de chargement", "Impossible de charger les horaires: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displaySchedules(List<Schedule> schedulesToDisplay) {
        if (scheduleContainer != null) {
            scheduleContainer.getChildren().clear();

            if (schedulesToDisplay.isEmpty()) {
                Label noSchedulesLabel = new Label("Aucun horaire trouvé");
                noSchedulesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");
                scheduleContainer.getChildren().add(noSchedulesLabel);
            } else {
                for (Schedule schedule : schedulesToDisplay) {
                    scheduleContainer.getChildren().add(createScheduleCard(schedule));
                }
            }
        }
    }

    private void filterSchedules(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            displaySchedules(schedules);
            return;
        }

        List<Schedule> filteredSchedules = schedules.stream()
                .filter(schedule ->
                        formatDayName(schedule.getDay()).toLowerCase().contains(searchText.toLowerCase()) ||
                                formatTime(schedule.getOpeningTime()).contains(searchText) ||
                                formatTime(schedule.getClosingTime()).contains(searchText)
                )
                .toList();

        displaySchedules(filteredSchedules);
    }

    private void filterSchedulesByDay(String dayName) {
        if (dayName == null || dayName.equals("Tous les jours")) {
            displaySchedules(schedules);
            return;
        }

        // Convert day name to enum
        Days selectedDay = null;
        switch (dayName) {
            case "Lundi": selectedDay = Days.MONDAY; break;
            case "Mardi": selectedDay = Days.TUESDAY; break;
            case "Mercredi": selectedDay = Days.WEDNESDAY; break;
            case "Jeudi": selectedDay = Days.THURSDAY; break;
            case "Vendredi": selectedDay = Days.FRIDAY; break;
            case "Samedi": selectedDay = Days.SATURDAY; break;
            case "Dimanche": selectedDay = Days.SUNDAY; break;
        }

        final Days day = selectedDay;
        List<Schedule> filteredSchedules = schedules.stream()
                .filter(schedule -> schedule.getDay() == day)
                .toList();

        displaySchedules(filteredSchedules);
    }

    private VBox createScheduleCard(Schedule schedule) {
        VBox scheduleBox = new VBox();
        scheduleBox.setPrefWidth(250);
        scheduleBox.setMaxWidth(250);
        scheduleBox.setMinHeight(180);
        scheduleBox.getStyleClass().add("schedule-card");
        scheduleBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");

        // Day header with gradient background
        VBox scheduleHeader = new VBox();
        scheduleHeader.setAlignment(Pos.CENTER);
        scheduleHeader.setStyle("-fx-background-color:rgba(255, 165, 0, 0.5);" +
                "-fx-border-radius: 12px 12px 0 0; " +
                "-fx-background-radius: 12px 12px 0 0; " +
                "-fx-padding: 15 15;");

        // Add edit button to the top-right corner
        StackPane headerWithButton = new StackPane();

        // Day display with icon
        HBox dayContainer = new HBox();
        dayContainer.setAlignment(Pos.CENTER);
        dayContainer.setSpacing(10);

        Label calendarIcon = new Label("📅");
        calendarIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #000000;");

        Label dayLabel = new Label(formatDayName(schedule.getDay()));
        dayLabel.setStyle("-fx-text-fill: Black; -fx-font-size: 20px; -fx-font-weight: bold;");

        dayContainer.getChildren().addAll(calendarIcon, dayLabel);

        // Create edit button
        Button editButton = new Button("✏️");
        editButton.setStyle("-fx-background-color: transparent; " +
                "-fx-font-size: 16px; " +
                "-fx-cursor: hand;");

        // Add hover effect for edit button
        editButton.setOnMouseEntered(e ->
                editButton.setStyle("-fx-background-color: rgba(0,0,0,0.1); " +
                        "-fx-font-size: 16px; " +
                        "-fx-cursor: hand;"));

        editButton.setOnMouseExited(e ->
                editButton.setStyle("-fx-background-color: transparent; " +
                        "-fx-font-size: 16px; " +
                        "-fx-cursor: hand;"));

        // Add click event for edit button
        editButton.setOnAction(e -> {
            showEditScheduleDialog(schedule);
        });

        // Position the edit button at the top right
        StackPane.setAlignment(editButton, Pos.TOP_RIGHT);
        StackPane.setMargin(editButton, new Insets(5, 5, 0, 0));

        // Add day container and edit button to the header layout
        headerWithButton.getChildren().addAll(dayContainer, editButton);
        scheduleHeader.getChildren().add(headerWithButton);

        // Schedule content
        VBox scheduleContent = new VBox();
        scheduleContent.setSpacing(15);
        scheduleContent.setAlignment(Pos.CENTER);
        scheduleContent.setStyle("-fx-background-color: white; " +
                "-fx-padding: 20 15; " +
                "-fx-border-radius: 0 0 12px 12px; " +
                "-fx-background-radius: 0 0 12px 12px;");

        // Opening hours with icons and better formatting
        HBox openingTimeBox = new HBox();
        openingTimeBox.setAlignment(Pos.CENTER_LEFT);
        openingTimeBox.setSpacing(12);

        Label openIcon = new Label("🕒");
        openIcon.setStyle("-fx-font-size: 16px;");

        Label openingLabel = new Label("Ouverture:");
        openingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        Label openingTime = new Label(formatTime(schedule.getOpeningTime()));
        openingTime.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        openingTimeBox.getChildren().addAll(openIcon, openingLabel, openingTime);

        // Closing hours
        HBox closingTimeBox = new HBox();
        closingTimeBox.setAlignment(Pos.CENTER_LEFT);
        closingTimeBox.setSpacing(12);

        Label closeIcon = new Label("🕘");
        closeIcon.setStyle("-fx-font-size: 16px;");

        Label closingLabel = new Label("Fermeture:");
        closingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        Label closingTime = new Label(formatTime(schedule.getClosingTime()));
        closingTime.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        closingTimeBox.getChildren().addAll(closeIcon, closingLabel, closingTime);

        // Current status indicator (open/closed)
        HBox statusBox = new HBox();
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setSpacing(8);
        statusBox.setStyle("-fx-padding: 8 0 0 0;");

        String statusText = isCurrentlyOpen(schedule) ? "OUVERT" : "FERMÉ";
        String statusColor = isCurrentlyOpen(schedule) ? "#27ae60" : "#bc1000";

        Label statusLabel = new Label(statusText);
        statusLabel.setStyle("-fx-background-color: " + statusColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 5 20; " +
                "-fx-background-radius: 30;");

        statusBox.getChildren().add(statusLabel);

        scheduleContent.getChildren().addAll(openingTimeBox, closingTimeBox, statusBox);
        scheduleBox.getChildren().addAll(scheduleHeader, scheduleContent);

        // Hover effects
        scheduleBox.setOnMouseEntered(e -> {
            scheduleBox.setStyle(scheduleBox.getStyle() +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3); " +
                    "-fx-translate-y: -5;");
        });

        scheduleBox.setOnMouseExited(e -> {
            scheduleBox.setStyle(scheduleBox.getStyle().replace(
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3); -fx-translate-y: -5;",
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);"));
        });

        // We still keep this click event for the entire card for better UX
        scheduleBox.setOnMouseClicked(e -> {
            showEditScheduleDialog(schedule);
        });

        return scheduleBox;
    }
    // Helper methods
    private String formatDayName(Days day) {
        switch(day) {
            case MONDAY: return "Lundi";
            case TUESDAY: return "Mardi";
            case WEDNESDAY: return "Mercredi";
            case THURSDAY: return "Jeudi";
            case FRIDAY: return "Vendredi";
            case SATURDAY: return "Samedi";
            case SUNDAY: return "Dimanche";
            default: return day.toString();
        }
    }

    private String formatTime(Time time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.toLocalTime().format(formatter);
    }

    private boolean isCurrentlyOpen(Schedule schedule) {
        LocalTime now = LocalTime.now();
        LocalTime openTime = schedule.getOpeningTime().toLocalTime();
        LocalTime closeTime = schedule.getClosingTime().toLocalTime();

        return (now.isAfter(openTime) && now.isBefore(closeTime));
    }

    private void showEditScheduleDialog(Schedule schedule) {
        Dialog<Pair<Time, Time>> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'horaire");


        // Custom header with specified orange color
        Label headerLabel = new Label("Modifier l'horaire pour " + formatDayName(schedule.getDay()));
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        HBox headerBox = new HBox(headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(15));
        headerBox.setStyle("-fx-background-color: rgba(255, 165, 0, 0.5); -fx-background-radius: 5 5 0 0;");

        // Set the button types with custom styling
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonType.OK.getButtonData());
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonType.CANCEL.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Apply custom styling to buttons after dialog is shown
        Platform.runLater(() -> {
            Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);

            saveButton.setStyle("-fx-background-color: rgba(255, 165, 0, 0.8); -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-background-radius: 5;");
            cancelButton.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333; -fx-background-radius: 5;");

            // Add hover effects
            saveButton.setOnMouseEntered(e -> saveButton.setStyle("-fx-background-color: rgba(255, 165, 0, 1.0); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));
            saveButton.setOnMouseExited(e -> saveButton.setStyle("-fx-background-color: rgba(255, 165, 0, 0.8); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));

            cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #d0d0d0; " +
                    "-fx-text-fill: #333333; -fx-background-radius: 5;"));
            cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #e0e0e0; " +
                    "-fx-text-fill: #333333; -fx-background-radius: 5;"));
        });

        // Create the form with time fields using the specified blue color
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82);");

        // Create styled time input fields
        TextField openingTimeField = createStyledTextField(formatTime(schedule.getOpeningTime()));
        TextField closingTimeField = createStyledTextField(formatTime(schedule.getClosingTime()));

        // Create styled labels
        Label openingLabel = new Label("Heure d'ouverture (HH:mm):");
        openingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label closingLabel = new Label("Heure de fermeture (HH:mm):");
        closingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Add clock icons
        Label openClockIcon = new Label("🕒");
        Label closeClockIcon = new Label("🕘");
        openClockIcon.setStyle("-fx-font-size: 16px;");
        closeClockIcon.setStyle("-fx-font-size: 16px;");

        // Create HBoxes for labels with icons
        HBox openingLabelBox = new HBox(10, openClockIcon, openingLabel);
        openingLabelBox.setAlignment(Pos.CENTER_LEFT);

        HBox closingLabelBox = new HBox(10, closeClockIcon, closingLabel);
        closingLabelBox.setAlignment(Pos.CENTER_LEFT);

        grid.add(openingLabelBox, 0, 0);
        grid.add(openingTimeField, 1, 0);
        grid.add(closingLabelBox, 0, 1);
        grid.add(closingTimeField, 1, 1);

        // Create a VBox to contain the header and grid
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(headerBox, grid);
        contentBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        dialog.getDialogPane().setContent(contentBox);

        // Add some styling to the dialog pane
        dialog.getDialogPane().setStyle("-fx-background-color: transparent;");
        dialog.getDialogPane().setPrefWidth(450);
        dialog.getDialogPane().setPrefHeight(250);

        // Convert the result to sql.Time when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    LocalTime openTime = LocalTime.parse(openingTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime closeTime = LocalTime.parse(closingTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));

                    return new Pair<>(Time.valueOf(openTime), Time.valueOf(closeTime));
                } catch (Exception e) {
                    showAlert("Erreur de format", "Veuillez entrer les heures au format HH:mm (ex: 09:00)");
                    return null;
                }
            }
            return null;
        });

        // Display the dialog and process the result
        dialog.showAndWait().ifPresent(times -> {
            if (times != null) {
                // Update the schedule with new times
                schedule.setOpeningTime(times.getKey());
                schedule.setClosingTime(times.getValue());

                // Update the schedule in the database
                updateScheduleInDatabase(schedule);
            }
        });
    }

    // Helper method to create consistently styled text fields
    private TextField createStyledTextField(String initialValue) {
        TextField textField = new TextField(initialValue);
        textField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 8; " +
                "-fx-font-size: 14px;");

        // Add focus effects
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                textField.setStyle("-fx-background-color: white; " +
                        "-fx-border-color: rgba(255, 165, 0, 0.8); " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px;");
            } else {
                textField.setStyle("-fx-background-color: white; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px;");
            }
        });

        return textField;
    }

    private void updateScheduleInDatabase(Schedule schedule) {
        try {
            scheduleService.update(schedule);

            showAlert("Succès", "Horaire mis à jour avec succès", Alert.AlertType.INFORMATION);

            // Refresh the UI
            refreshScheduleDisplay();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de mettre à jour l'horaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshScheduleDisplay() {
        // Reload all schedules
        loadSchedules();

        // Re-apply any filters if needed
        if (dayFilter != null && !dayFilter.getValue().equals("Tous les jours")) {
            filterSchedulesByDay(dayFilter.getValue());
        }
    }

    private void showAlert(String title, String content) {
        showAlert(title, content, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}