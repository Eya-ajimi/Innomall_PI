package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.Product;
import tn.esprit.services.EventService;

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
    @FXML
    public void initialize() {

        try {
            List<Event> event =eventService.getEventsByShopId(1);
            loadEvents();

            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                eventContainer.getChildren().clear();
                try {
                    List<Event> filteredProducts =eventService.getEventsByShopId(1).stream()
                            .filter(e -> e.getEventTitle().toLowerCase().contains(newValue.toLowerCase()))
                            .toList();
                    displayProducts(filteredProducts);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
        }
        addButton.setOnAction(event -> showAddEvenementPopup());
        // Button hover & click effects
        addButton.setOnMouseEntered(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseExited(event -> addButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff791f; -fx-background-radius: 4;"));
        addButton.setOnMousePressed(event -> addButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4;"));
        addButton.setOnMouseReleased(event -> addButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));

    }
    //----------------------------------------------------
    private void loadEvents() {
        try {
            List<Event> events = eventService.getEventsByShopId(1);
            displayProducts(events);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------
    private void displayProducts( List<Event> events) {
        for (Event event : events) {
            VBox productBox = createEvenetCard(event);
            eventContainer.getChildren().add(productBox);
        }
    }
    //----------------------------------------------------
    private VBox createEvenetCard(Event event) {
        VBox productBox = new VBox();

        productBox.setPrefWidth(300);
        productBox.setMaxWidth(400);
        productBox.setMinHeight(200);
        productBox.getStyleClass().add("event-card");
        productBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 15px; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

        // ------Event header
        VBox productHeader = new VBox();
        productHeader.setStyle("-fx-background-color: rgba(255, 165, 0, 0.50); " +
                "-fx-border-radius: 15px 15px 0 0; " +
                "-fx-background-radius: 15px 15px 0 0; " +
                "-fx-padding: 12 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 0 0 1 0;");
        //event title
        Label titlelabel = new Label("Titre de l'evenement:\n" + event.getEventTitle());
        titlelabel.setStyle(" -fx-text-fill: #000000; -fx-font-size: 15px;");
        productHeader.getChildren().add(titlelabel);

        //------ Event body
        VBox productBody = new VBox();
        productBody.setStyle("-fx-padding: 15;");

        Label organizorlable = new Label("Id Organisateur: " + event.getDescription());
        organizorlable.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 14px;");
        organizorlable.setWrapText(true);
        Label descriptionlabel = new Label("Id Organisateur: " + event.getIdOrganizer());
        descriptionlabel.setStyle("-fx-text-fill: #000000; -fx-wrap-text: true; -fx-font-size: 12px;");
        descriptionlabel.setWrapText(true);

        productBody.getChildren().addAll(organizorlable,descriptionlabel);

        // Product footer
        VBox productFooter = new VBox();
        productFooter.setStyle("-fx-background-color: rgb(255,255,255); " +
                "-fx-border-radius: 0 0 15px 15px; " +
                "-fx-background-radius: 0 0 15px 15px; " +
                "-fx-padding: 10 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1 0 0 0;");
        productFooter.setMinHeight(5);  // did this to to avoid excess space

        Label startlabel = new Label("Commence en : " + event.getStart() );
        startlabel.setStyle("-fx-font-weight: light; -fx-text-fill: #000000; -fx-font-size: 12px;");
        Label endlable = new Label("Se termine en : " + event.getEnd());
        endlable.setStyle("-fx-font-weight: light; -fx-text-fill: #000000; -fx-font-size: 12px;");

        Label placelabel = new Label("L'emplacement: " + event.getPlace());
        placelabel.setStyle("-fx-font-weight:bold; -fx-text-fill: #000000; -fx-font-size: 14px;");

        productFooter.getChildren().addAll(startlabel,endlable, placelabel);

        // Add all sections to the product card
        productBox.getChildren().addAll(productHeader, productBody, productFooter);

        // Add hover effect
        productBox.setOnMouseEntered(e -> {
            productBox.setStyle(productBox.getStyle() +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                    "-fx-translate-y: -5;");
        });

        productBox.setOnMouseExited(e -> {
            productBox.setStyle(productBox.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5); " +
                    "-fx-translate-y: -2;", "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        });

        return productBox;
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
