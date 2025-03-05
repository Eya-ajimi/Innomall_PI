package tn.esprit.gui.oussemacontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import tn.esprit.entities.Event;
import tn.esprit.services.OussemaService.EventService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ClientEventViewController {

    @FXML
    private VBox postsContainer;

    @FXML private Label homeLabel;

    @FXML private Label shopsLabel;
    @FXML private Label parkingLabel;
    @FXML private Label cartLabel;

    @FXML
    private ImageView userProfileImage;

    @FXML
    private FlowPane eventsContainer;


    @FXML
    private DatePicker searchDatePicker;

    @FXML
    private EventService eventService = new EventService();



    @FXML
    private Label eventsLabel; // Add this if not already present



    @FXML
    public void initialize() {
        loadEvents(null); // Load all events initially

        // Set the onMouseClicked event for the user profile image
        userProfileImage.setOnMouseClicked(this::handleUserProfileClick);

        // Add event handler to the Home label
        homeLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Homepage.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Navigation Error", "Failed to load the homepage.");
            }
        });
        // Add event handler to the Shops label
        shopsLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shops.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cartLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Panier.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        parkingLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/parking_lot.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handlePanierClick() {
        try {
            // Load the Shops.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Panier.fxml"));
            Pane shopsPage = loader.load();

            // Get the current stage
            Stage stage = (Stage) postsContainer.getScene().getWindow(); // Use an existing node to get the stage
            stage.setScene(new Scene(shopsPage));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShopsClick(MouseEvent event) {
        try {
            // Load the Shops.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Shops.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load the shops page.");
        }
    }

    @FXML
    private void handleEventsClick(MouseEvent event) {
        try {
            // Load the client_event_view.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client_event_view.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void handleSearch() {
        LocalDate selectedDate = searchDatePicker.getValue();
        loadEvents(selectedDate);
    }

    private void loadEvents(LocalDate date) {
        eventsContainer.getChildren().clear(); // Clear existing cards
        List<Event> events = date == null ? eventService.getAll() : eventService.getEventsByDate(date);

        for (Event event : events) {
            try {
                // Load the client event card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client_event_card.fxml"));
                Parent eventCard = loader.load();

                // Set the event data in the card
                ClientEventCardController controller = loader.getController();
                controller.setEvent(event);

                // Add the card to the container
                eventsContainer.getChildren().add(eventCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    @FXML
    private void handleUserProfileClick(MouseEvent event) {
        try {
            // Load the UserDashboard.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) userProfileImage.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load the user dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}