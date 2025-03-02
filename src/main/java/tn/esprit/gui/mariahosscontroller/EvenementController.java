package tn.esprit.gui.mariahosscontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.services.mariahossservice.*;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EvenementController {

    @FXML
    private FlowPane eventContainer;
    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;

    private final EventService eventService = new EventService();
    private Session session = Session.getInstance();

    @FXML
    public void initialize() {
        try {
            List<Event> events = eventService.getEventsByShopId(session.getCurrentUser().getId()); // Use current user's ID
            loadEvents();

            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                eventContainer.getChildren().clear();
                try {
                    List<Event> filteredEvents = eventService.getEventsByShopId(session.getCurrentUser().getId()).stream()
                            .filter(e -> e.getEventTitle().toLowerCase().contains(newValue.toLowerCase()))
                            .toList();
                    displayEvents(filteredEvents);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addButton.setOnAction(event -> showAddEvenementPopup());

        // Button hover + click effects
        addButton.setOnMouseEntered(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseExited(event -> addButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff791f; -fx-background-radius: 4;"));
        addButton.setOnMousePressed(event -> addButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseReleased(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
    }

    private void loadEvents() {
        try {
            List<Event> events = eventService.getEventsByShopId(session.getCurrentUser().getId()); // Use current user's ID
            displayEvents(events);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayEvents(List<Event> events) {
        for (Event event : events) {
            VBox eventBox = createEventCard(event);
            eventContainer.getChildren().add(eventBox);
        }
    }
    //----------------------------------------------------
    private void displayProducts( List<Event> events) {
        for (Event event : events) {
            VBox productBox = createEventCard(event);
            eventContainer.getChildren().add(productBox);
        }
    }

    private VBox createEventCard(Event event) {
        VBox productBox = new VBox();
        productBox.setPrefWidth(300);
        productBox.setMaxWidth(400);
        productBox.setMinHeight(200);
        productBox.getStyleClass().add("event-card");
        productBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 15px; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);");

        // ------Event header with the specified orange shade and action buttons
        HBox productHeader = new HBox();
        productHeader.setStyle("-fx-background-color: rgba(255, 165, 0, 0.75); " +
                "-fx-border-radius: 15px 15px 0 0; " +
                "-fx-background-radius: 15px 15px 0 0; " +
                "-fx-padding: 15 20; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");

        // Left side - Event title
        VBox titleBox = new VBox();
        titleBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleBox, javafx.scene.layout.Priority.ALWAYS);

        Label titleLabel = new Label(event.getEventTitle());
        titleLabel.setStyle("-fx-text-fill: #333333; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleBox.getChildren().add(titleLabel);

        // Right side - Edit and Delete buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);

        // Edit button with image icon
        Button editButton = new Button();
        ImageView editIcon = new ImageView(new Image("/assets/edit.png"));
        editIcon.setFitHeight(16);
        editIcon.setFitWidth(16);
        editButton.setGraphic(editIcon);
        editButton.setStyle("-fx-background-color: transparent; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 5; " +
                "-fx-background-radius: 100;");

        // Add hover effect for edit button
        editButton.setOnMouseEntered(e -> {
            editButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 5; " +
                    "-fx-background-radius: 100;");
        });

        editButton.setOnMouseExited(e -> {
            editButton.setStyle("-fx-background-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 5; " +
                    "-fx-background-radius: 100;");
        });

        // Edit button action
        editButton.setOnAction(e -> {
            // Add edit action here
            System.out.println("Edit event: " + event.getEventTitle());
        });

        // Delete button with image icon
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image("/assets/trash.png"));
        deleteIcon.setFitHeight(16);
        deleteIcon.setFitWidth(16);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: transparent; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 5; " +
                "-fx-background-radius: 100;");

        // Add hover effect for delete button
        deleteButton.setOnMouseEntered(e -> {
            deleteButton.setStyle("-fx-background-color: rgba(255,0,0,0.1); " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 5; " +
                    "-fx-background-radius: 100;");
        });

        deleteButton.setOnMouseExited(e -> {
            deleteButton.setStyle("-fx-background-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 5; " +
                    "-fx-background-radius: 100;");
        });

        // Delete button action
        deleteButton.setOnAction(e -> {
            // Add delete action here
            System.out.println("Delete event: " + event.getEventTitle());
        });

        actionButtons.getChildren().addAll(editButton, deleteButton);
        productHeader.getChildren().addAll(titleBox, actionButtons);

        // ------ Event body with the specified blue shade
        VBox productBody = new VBox(10); // Added spacing between elements
        productBody.setStyle("-fx-background-color: rgba(191, 226, 246, 0.82); " +
                "-fx-padding: 15 20;");

        // Description with icon and better formatting
        HBox descriptionBox = new HBox(8);
        Label descriptionLabel = new Label("Description: ");
        descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 14px;");
        Label descriptionValue = new Label(event.getDescription());
        descriptionValue.setStyle("-fx-text-fill: #333333; -fx-wrap-text: true; -fx-font-size: 14px;");
        descriptionValue.setWrapText(true);
        descriptionBox.getChildren().addAll(descriptionLabel, descriptionValue);

        // Organizer info with icon
        HBox organizerBox = new HBox(8);
        Label organizerLabel = new Label("Organisateur: ");
        organizerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 14px;");

        organizerBox.getChildren().addAll(organizerLabel);

        productBody.getChildren().addAll(descriptionBox, organizerBox);

        // Product footer with improved design
        VBox productFooter = new VBox(8); // Added spacing between elements
        productFooter.setStyle("-fx-background-color: white; " +
                "-fx-border-radius: 0 0 15px 15px; " +
                "-fx-background-radius: 0 0 15px 15px; " +
                "-fx-padding: 15 20; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1 0 0 0;");

        // Place info with icon
        HBox placeBox = new HBox(8);
        Label placeIcon = new Label("📍");
        placeIcon.setStyle("-fx-font-size: 16px;");
        Label placeLabel = new Label(event.getPlace());
        placeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 15px;");
        placeBox.getChildren().addAll(placeIcon, placeLabel);

        // Time info with improved formatting and icons
        HBox timeBox = new HBox(15);
        timeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Format the Timestamp objects to strings
        String startTimeStr = event.getStart() != null ? formatTimestamp(event.getStart()) : "N/A";
        String endTimeStr = event.getEnd() != null ? formatTimestamp(event.getEnd()) : "N/A";

        // Start time
        VBox startBox = new VBox(2);
        Label startIcon = new Label("🕒");
        startIcon.setStyle("-fx-font-size: 14px;");
        Label startTitle = new Label("Début:");
        startTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555; -fx-font-size: 12px;");
        Label startValue = new Label(startTimeStr);
        startValue.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px;");
        startBox.getChildren().addAll(startTitle, startValue);

        // End time
        VBox endBox = new VBox(2);
        Label endTitle = new Label("Fin:");
        endTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555; -fx-font-size: 12px;");
        Label endValue = new Label(endTimeStr);
        endValue.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px;");
        endBox.getChildren().addAll(endTitle, endValue);

        // Add icon and both time boxes to the timeBox
        timeBox.getChildren().addAll(startIcon, startBox, endBox);

        productFooter.getChildren().addAll(placeBox, timeBox);

        // Add all sections to the product card
        productBox.getChildren().addAll(productHeader, productBody, productFooter);

        // Improved hover effect with smooth transition
        String baseStyle = productBox.getStyle();
        String hoverStyle = baseStyle +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 4); " +
                "-fx-translate-y: -3; " +
                "-fx-cursor: hand;";

        productBox.setStyle(baseStyle + "-fx-transition: all 0.3s ease;");

        productBox.setOnMouseEntered(e -> {
            productBox.setStyle(hoverStyle);
        });

        productBox.setOnMouseExited(e -> {
            productBox.setStyle(baseStyle);
        });

        return productBox;
    }

    // Helper method to format Timestamp to a readable string
    private String formatTimestamp(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "N/A";

        // Create a formatter for the date and time
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(timestamp);
    }

    // Helper method to create icon labels with consistent styling
    private Label createIcon(String iconText, int size) {
        Label icon = new Label(iconText);
        icon.setStyle("-fx-font-size: " + size + "px;");
        return icon;
    }
    private void showAddEvenementPopup() {
        try {
            // Change this line
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutEvenement.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ajouter Un Evenement");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            loadEvents();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }
}
