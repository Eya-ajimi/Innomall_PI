package tn.esprit.gui.oussemacontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.OussemaService.EventService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.util.List;

public class EventController {

    @FXML
    private FlowPane eventContainer;

    @FXML
    private TextField searchField;

    @FXML
    private Button addButton;

    private final EventService eventService = new EventService();
    private final Session session = Session.getInstance(); // Session instance

    @FXML
    public void initialize() {
        // Fetch events for the current organizer
        loadEvents();

        // Add a listener to the search field to filter events
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            eventContainer.getChildren().clear();
            List<Event> filteredEvents = eventService.getEventsByOrganizer(session.getCurrentUser().getId()).stream()
                    .filter(e -> e.getDescription().toLowerCase().contains(newValue.toLowerCase()))
                    .toList();
            displayEvents(filteredEvents);
        });

        // Set up the "Add Event" button
        addButton.setOnAction(event -> showAddEvenementPopup());

        // Button hover + click effects
        addButton.setOnMouseEntered(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseExited(event -> addButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff791f; -fx-background-radius: 4;"));
        addButton.setOnMousePressed(event -> addButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseReleased(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
    }

    /**
     * Load events from the database and display them in the FlowPane.
     */
    private void loadEvents() {
        // Fetch events for the current organizer
        List<Event> events = eventService.getEventsByOrganizer(session.getCurrentUser().getId());

        // Debugging: Print the fetched events
        System.out.println("Fetched events: " + events);

        // Display the events in the UI
        displayEvents(events);
    }

    /**
     * Display the given list of events in the FlowPane.
     */
    private void displayEvents(List<Event> events) {
        eventContainer.getChildren().clear(); // Clear existing cards
        for (Event event : events) {
            VBox eventCard = createEventCard(event); // Create a card for each event
            eventContainer.getChildren().add(eventCard); // Add the card to the container
        }

        // Debugging: Print the number of events displayed
        System.out.println("Number of events displayed: " + events.size());
    }

    /**
     * Create a card for the given event.
     */
    private VBox createEventCard(Event event) {
        VBox eventBox = new VBox();
        // Make cards smaller
        eventBox.setPrefWidth(400);
        eventBox.setMaxWidth(400);
        eventBox.setMinHeight(150);
        eventBox.getStyleClass().add("event-card");
        eventBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 3, 0, 0, 1);");

        // ------ Event header with orange shade and action buttons
        HBox eventHeader = new HBox();
        eventHeader.setStyle("-fx-background-color: rgba(255, 165, 0, 0.75); " +
                "-fx-border-radius: 12px 12px 0 0; " +
                "-fx-background-radius: 12px 12px 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");

        // Left side - Event title
        VBox titleBox = new VBox();
        titleBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleBox, javafx.scene.layout.Priority.ALWAYS);

        Label titleLabel = new Label(event.getDescription());
        titleLabel.setStyle("-fx-text-fill: #333333; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleBox.getChildren().add(titleLabel);

        // Right side - Edit and Delete buttons
        HBox actionButtons = new HBox(8);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);

        // Edit button with image icon
        Button editButton = new Button();
        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/edit.png")));
        editIcon.setFitHeight(14);
        editIcon.setFitWidth(14);
        editButton.setGraphic(editIcon);
        editButton.setStyle("-fx-background-color: transparent; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 4; " +
                "-fx-background-radius: 100;");

        // Add hover effect for edit button with light blue
        editButton.setOnMouseEntered(e -> {
            editButton.setStyle("-fx-background-color: rgba(191, 226, 246, 0.4); " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 4; " +
                    "-fx-background-radius: 100;");
        });

        editButton.setOnMouseExited(e -> {
            editButton.setStyle("-fx-background-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 4; " +
                    "-fx-background-radius: 100;");
        });

        // Link editButton to handleModifyEvent
        editButton.setOnAction(e -> handleModifyEvent(event));

        // Delete button with image icon
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/trash.png")));
        deleteIcon.setFitHeight(14);
        deleteIcon.setFitWidth(14);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: transparent; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 4; " +
                "-fx-background-radius: 100;");

        // Add hover effect for delete button
        deleteButton.setOnMouseEntered(e -> {
            deleteButton.setStyle("-fx-background-color: rgba(255,0,0,0.1); " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 4; " +
                    "-fx-background-radius: 100;");
        });

        deleteButton.setOnMouseExited(e -> {
            deleteButton.setStyle("-fx-background-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 4; " +
                    "-fx-background-radius: 100;");
        });

        // Link deleteButton to handleDeleteEvent
        deleteButton.setOnAction(e -> handleDeleteEvent(event));

        actionButtons.getChildren().addAll(editButton, deleteButton);
        eventHeader.getChildren().addAll(titleBox, actionButtons);

        // ------ Event body with white background
        VBox eventBody = new VBox(8); // Reduced spacing
        eventBody.setStyle("-fx-background-color: white; " +
                "-fx-padding: 12 15;");

        // Description with light blue border instead of background
        HBox descriptionBox = new HBox(6);
        descriptionBox.setStyle("-fx-border-color: #bfe2f6; " +
                "-fx-border-width: 1px; " +
                "-fx-padding: 6; " +
                "-fx-border-radius: 4;");

        // Description icon
        Label descIcon = new Label("📝");
        descIcon.setStyle("-fx-font-size: 12px;");

        Label descriptionLabel = new Label("Description: ");
        descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 12px;");
        Label descriptionValue = new Label(event.getDescription());
        descriptionValue.setStyle("-fx-text-fill: #333333; -fx-wrap-text: true; -fx-font-size: 12px;");
        descriptionValue.setWrapText(true);
        descriptionBox.getChildren().addAll(descIcon, descriptionLabel, descriptionValue);

        // Dates with light blue border
        HBox dateDebutBox = new HBox(6);
        dateDebutBox.setStyle("-fx-border-color: #bfe2f6; " +
                "-fx-border-width: 1px; " +
                "-fx-padding: 6; " +
                "-fx-border-radius: 4;");

        // Start date icon
        Label startIcon = new Label("🗓️");
        startIcon.setStyle("-fx-font-size: 12px;");

        Label dateDebutLabelText = new Label("Date de début: ");
        dateDebutLabelText.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 12px;");
        Label dateDebutValue = new Label(event.getDateDebut());
        dateDebutValue.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px;");
        dateDebutBox.getChildren().addAll(startIcon, dateDebutLabelText, dateDebutValue);

        HBox dateFinBox = new HBox(6);
        dateFinBox.setStyle("-fx-border-color: #bfe2f6; " +
                "-fx-border-width: 1px; " +
                "-fx-padding: 6; " +
                "-fx-border-radius: 4;");

        // End date icon
        Label endIcon = new Label("🏁");
        endIcon.setStyle("-fx-font-size: 12px;");

        Label dateFinLabelText = new Label("Date de fin: ");
        dateFinLabelText.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 12px;");
        Label dateFinValue = new Label(event.getDateFin());
        dateFinValue.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px;");
        dateFinBox.getChildren().addAll(endIcon, dateFinLabelText, dateFinValue);

        // Add spacing between the elements in eventBody
        VBox.setMargin(dateDebutBox, new Insets(4, 0, 0, 0));
        VBox.setMargin(dateFinBox, new Insets(4, 0, 0, 0));

        eventBody.getChildren().addAll(descriptionBox, dateDebutBox, dateFinBox);

        // ------ Add event footer with location
        VBox eventFooter = new VBox(8);
        eventFooter.setStyle("-fx-background-color: rgba(191, 226, 246, 0.25); " +
                "-fx-border-radius: 0 0 12px 12px; " +
                "-fx-background-radius: 0 0 12px 12px; " +
                "-fx-padding: 10 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1 0 0 0;");

        // Location with icon
        HBox locationBox = new HBox(6);
        locationBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Location icon
        Label locationIcon = new Label("📍");
        locationIcon.setStyle("-fx-font-size: 12px;");

        Label locationLabel = new Label("Emplacement: ");
        locationLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 12px;");

        Label locationValue = new Label(event.getEmplacement());
        locationValue.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px;");
        locationValue.setWrapText(true);

        locationBox.getChildren().addAll(locationIcon, locationLabel, locationValue);
        eventFooter.getChildren().add(locationBox);

        // Add all sections to the event card
        eventBox.getChildren().addAll(eventHeader, eventBody, eventFooter);

        // Improved hover effect with smooth transition and light blue accent
        String baseStyle = eventBox.getStyle();
        String hoverStyle = baseStyle +
                "-fx-border-color: #bfe2f6; " +
                "-fx-effect: dropshadow(gaussian, rgba(191, 226, 246, 0.5), 6, 0, 0, 3); " +
                "-fx-translate-y: -2; " +
                "-fx-cursor: hand;";

        eventBox.setStyle(baseStyle + "-fx-transition: all 0.3s ease;");

        eventBox.setOnMouseEntered(e -> {
            eventBox.setStyle(hoverStyle);
        });

        eventBox.setOnMouseExited(e -> {
            eventBox.setStyle(baseStyle);
        });

        return eventBox;
    }
    /**
     * Show the "Add Event" popup.
     */
    private void showAddEvenementPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event_form.fxml"));
            Parent root = loader.load();

            // Get the controller for the form
            EventFormController formController = loader.getController();

            // Initialize a new event and pass it to the form controller
            Event newEvent = new Event();
            formController.setEvent(newEvent);

            // Fetch the current user from the session
            Utilisateur currentUser = session.getCurrentUser();
            if (currentUser != null) {
                formController.setIdOrganisateur(currentUser.getId());
            }

            // Open the form in a new window
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ajouter Un Evenement");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            // Refresh the event list after adding a new event
            if (formController.isUpdated()) {
                eventService.add(newEvent); // Add the new event to the database
                loadEvents(); // Refresh the event list
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }

    /**
     * Handle modifying an event.
     */
    public void handleModifyEvent(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event_form.fxml"));
            Parent root = loader.load();

            // Get the controller for the form
            EventFormController formController = loader.getController();

            // Pass the event to be modified to the form controller
            formController.setEvent(event);

            // Fetch the current user from the session
            Utilisateur currentUser = session.getCurrentUser();
            if (currentUser != null) {
                formController.setIdOrganisateur(currentUser.getId());
            }

            // Open the form in a new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modify Event");
            stage.showAndWait();

            // Refresh the event list after modification
            if (formController.isUpdated()) {
                // Get the updated event from the form controller
                Event updatedEvent = formController.getEvent();
                eventService.update(updatedEvent); // Update the event in the database
                loadEvents(); // Refresh the event list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle deleting an event.
     */
    public void handleDeleteEvent(Event event) {
        eventService.delete(event.getId());
        loadEvents();
    }
}