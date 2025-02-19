package tn.esprit.controllers;

import tn.esprit.entities.Schedule;
import tn.esprit.entities.Schedule;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Schedule;
import tn.esprit.entities.Schedule;
import tn.esprit.services.ScheduleService;
import tn.esprit.services.ScheduleService;

import java.sql.SQLException;
import java.util.List;

public class HorairesController {
    @FXML
    private FlowPane scheduleContainer;

    @FXML
    private TextField searchField;

    private final ScheduleService scheduleService = new ScheduleService();

    @FXML
    private Button addButton;

    public void initialize() {

        try {
            List<Schedule> schedules = scheduleService.getScheduleByShopOwnerId(1);
            loadSchedules();

//            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
//                productContainer.getChildren().clear();
//                try {
//                    List<Schedule> filteredSchedules = scheduleService.getScheduleByShopOwnerId(1).stream()
//                            .filter(s -> s.getDay().contains(newValue.toLowerCase()))
//                            .toList();
//                    displaySchedules(filteredSchedules);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
        } catch (SQLException e) {
        }
//        addButton.setOnAction(event -> showAddSchedulePopup());
        // Button hover & click effects
        addButton.setOnMouseEntered(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseExited(event -> addButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff791f; -fx-background-radius: 4;"));
        addButton.setOnMousePressed(event -> addButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseReleased(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));

    }

    private void loadSchedules() {
        try {
            List<Schedule> schedules = scheduleService.getScheduleByShopOwnerId(1);
            displaySchedule(schedules);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void displaySchedule(List<Schedule> products) {
        for (Schedule product : products) {
            VBox scheduleBox = createScheduleCard(product);
            scheduleContainer.getChildren().add(scheduleBox);
        }
    }
    private VBox createScheduleCard(Schedule schedule) {
        VBox scheduleBox = new VBox();

        scheduleBox.setPrefWidth(250);
        scheduleBox.setMaxWidth(250);
        scheduleBox.setMinHeight(100);
        scheduleBox.getStyleClass().add("product-card");
        scheduleBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 15px; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

        // Schedule header with ID
        VBox scheduleHeader = new VBox();
        scheduleHeader.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-border-radius: 15px 15px 0 0; " +
                "-fx-background-radius: 15px 15px 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");


        Label daylabel = new Label("Jour "+schedule.getDay());
        daylabel.setStyle("-fx-text-fill:#000000; -fx-font-size: 16px;");
        scheduleHeader.getChildren().add(daylabel);


// Schedule footer with price and stock
        VBox scheduleFooter = new VBox();
        scheduleFooter.setStyle("-fx-background-color: rgb(255,255,255); " +
                "-fx-border-radius: 0 0 15px 15px; " +
                "-fx-background-radius: 0 0 15px 15px; " +
                "-fx-padding: 10 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1 0 0 0;");
        scheduleFooter.setMinHeight(5);

        Label openingLabel = new Label("Overture" +schedule.getOpeningTime());


        Label closinglabel = new Label("Fermeture" +schedule.getClosingTime());

// Change stock label color based on stock level


        scheduleFooter.getChildren().addAll(openingLabel, closinglabel);
        scheduleBox.getChildren().addAll(scheduleHeader, scheduleFooter);


        scheduleBox.setOnMouseEntered(e -> {
            scheduleBox.setStyle(scheduleBox.getStyle() +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                    "-fx-translate-y: -5;");
        });

        scheduleBox.setOnMouseExited(e -> {
            scheduleBox.setStyle(scheduleBox.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                    "-fx-translate-y: -2;", "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        });

        return scheduleBox;
    }
}
