package tn.esprit.gui.sofienecontroller;

import javafx.scene.Node;
import javafx.scene.Parent;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.sofieneservice.EmailReservationService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import tn.esprit.services.sofieneservice.TwilioService;
import tn.esprit.utils.MQTTClient;
import tn.esprit.utils.MQTTMessageHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.PlaceParking;
import tn.esprit.entities.Reservation;
import tn.esprit.entities.enums.StatutPlace;
import tn.esprit.services.sofieneservice.PlaceParkingService;
import tn.esprit.services.sofieneservice.ReservationService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

// Import Logger and Level
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkingLotController implements Initializable {
    private static final Logger logger = Logger.getLogger(ParkingLotController.class.getName());
    private PlaceParkingService parkingService = new PlaceParkingService();
    private Image carImage;
    private Timeline pulseAnimation;
    private MQTTClient mqttClient;
    private MQTTMessageHandler mqttMessageHandler;
    private EmailReservationService emailReservationService = new EmailReservationService();
    @FXML private Button viewReservedSpotsButton;
    @FXML private Label counterLabel;
    @FXML private Label occupancyLabel;
    @FXML private VBox parkingArea;
    @FXML private HBox topRow;
    @FXML private HBox bottomRow;
    @FXML private Rectangle passage;
    @FXML private StackPane passageContainer;
    @FXML private StackPane statsCard1;
    @FXML private StackPane statsCard2;
    @FXML private ComboBox<String> floorComboBox;
    @FXML private Label homeLabel;
    @FXML private Label eventLabel;
    @FXML private Label cartLabel;
    @FXML private Label shopLabel;

    private int availableSpots = 0;
    private int totalSpots = 0;
    private Map<Integer, Boolean> animationPlayedMap = new HashMap<>();

    @FXML
    private void handleViewReservedSpotsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reserved_spots_dialog.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Reserved Spots");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            carImage = new Image(getClass().getResource("/assets/car.png").toExternalForm());
        } catch (Exception e) {
            System.out.println("Error loading car image: " + e.getMessage());
        }
        mqttMessageHandler = new MQTTMessageHandler(parkingService);
        mqttClient = new MQTTClient(mqttMessageHandler);
        initializeAnimations();
        initializeAutoRefresh();
        initializeFloorComboBox();
        loadParkingSpots();
        setupStatsCardAnimation(statsCard1);
        setupStatsCardAnimation(statsCard2);
        enhanceDrivingLane();

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
            }
        });
        shopLabel.setOnMouseClicked(event -> {
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
        eventLabel.setOnMouseClicked(event -> {
            try {
                // Load the Homepage.fxml file
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
    }

    private void enhanceDrivingLane() {
        // Create lane markers
        createLaneMarkers();

        // Add floating car animation
        createFloatingCarAnimation();
    }

    private void createLaneMarkers() {
        HBox markersContainer = new HBox();
        markersContainer.setAlignment(Pos.CENTER);
        markersContainer.setSpacing(40);

        for (int i = 0; i < 8; i++) {
            Rectangle marker = new Rectangle(20, 5);
            marker.setFill(Color.WHITE);
            marker.setArcWidth(5);
            marker.setArcHeight(5);
            markersContainer.getChildren().add(marker);

            // Create fading animation for each marker
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), marker);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.4);
            fadeTransition.setCycleCount(Timeline.INDEFINITE);
            fadeTransition.setAutoReverse(true);

            // Stagger the animations
            fadeTransition.setDelay(Duration.millis(i * 200));
            fadeTransition.play();
        }

        // Add markers to passage container
        passageContainer.getChildren().add(markersContainer);
    }

    private void createFloatingCarAnimation() {
        // Create small car for lane animation
        ImageView movingCar = new ImageView(carImage);
        movingCar.setFitWidth(100);
        movingCar.setFitHeight(100);
        movingCar.setPreserveRatio(true);
        movingCar.setRotate(90); // Rotate to face direction of travel

        // Add to passage container
        passageContainer.getChildren().add(movingCar);

        // Position car initially outside visible area
        movingCar.setTranslateX(-passage.getWidth()/2 - 100);

        // Create animation to move car across lane
        TranslateTransition carTransition = new TranslateTransition(Duration.seconds(7), movingCar);
        carTransition.setFromX(-passage.getWidth()/2 - 100);
        carTransition.setToX(passage.getWidth()/2 + 100);
        carTransition.setCycleCount(Timeline.INDEFINITE);
        carTransition.play();
    }

    private void initializeAnimations() {
        // Enhanced lane glow effect
        Timeline passageGlowAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(passage.opacityProperty(), 0.8)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(passage.opacityProperty(), 1))
        );
        passageGlowAnimation.setAutoReverse(true);
        passageGlowAnimation.setCycleCount(Timeline.INDEFINITE);
        passageGlowAnimation.play();

        // Enhanced pulse animation
        pulseAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(passage.scaleXProperty(), 1),
                        new KeyValue(passage.scaleYProperty(), 1)),
                new KeyFrame(Duration.seconds(0.7),
                        new KeyValue(passage.scaleXProperty(), 1.03),
                        new KeyValue(passage.scaleYProperty(), 1.05)),
                new KeyFrame(Duration.seconds(1.4),
                        new KeyValue(passage.scaleXProperty(), 1),
                        new KeyValue(passage.scaleYProperty(), 1))
        );
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.play();

        // Set gradient fill for passage
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#3498db", 0.8)),
                new Stop(0.5, Color.web("#2980b9", 0.9)),
                new Stop(1, Color.web("#3498db", 0.8))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, null, stops);
        passage.setFill(gradient);
    }

    private void setupStatsCardAnimation(StackPane card) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
        card.setOnMouseEntered(e -> {
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });
        card.setOnMouseExited(e -> {
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }

    private void initializeAutoRefresh() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> { // 5 seconds
            try {
                new ReservationService().updateExpiredReservations();
                loadParkingSpots(); // Refresh the parking spots display
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error updating expired reservations", e);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void initializeFloorComboBox() {
        floorComboBox.getItems().addAll("Level 1", "Level 2");
        floorComboBox.setValue("Level 1"); // Default selection
        floorComboBox.setOnAction(event -> loadParkingSpots());
    }

    private void loadParkingSpots() {
        try {
            // Update parking spot status based on reservations
            updateParkingSpotStatusBasedOnReservations();

            String selectedFloor = floorComboBox.getValue();
            List<PlaceParking> parkingSpots = parkingService.showAll();
            List<Reservation> reservations = new ReservationService().showAll(); // Fetch all reservations

            topRow.getChildren().clear();
            bottomRow.getChildren().clear();

            resetCounters();

            // Filter spots for the selected floor
            List<PlaceParking> filteredSpots = new ArrayList<>();
            for (PlaceParking spot : parkingSpots) {
                if (spot.getFloor().equals(selectedFloor)) {
                    filteredSpots.add(spot);
                }
            }

            totalSpots = filteredSpots.size();

            int half = filteredSpots.size() / 2;
            for (int i = 0; i < filteredSpots.size(); i++) {
                PlaceParking place = filteredSpots.get(i);
                StackPane spotContainer = createParkingSpot(place, reservations); // Pass reservations to createParkingSpot

                if (i < half) {
                    topRow.getChildren().add(spotContainer);
                } else {
                    bottomRow.getChildren().add(spotContainer);
                }
            }

            updateStatistics();
        } catch (SQLException e) {
            showErrorAlert("Error loading parking spots", e.getMessage());
        }
    }

    private void resetCounters() {
        availableSpots = 0;
        totalSpots = 0;
    }

    private StackPane createParkingSpot(PlaceParking place, List<Reservation> reservations) {
        StackPane spotContainer = new StackPane();
        spotContainer.getStyleClass().add("parking-spot");

        Rectangle spot = createSpotRectangle();
        ImageView carView = createCarImageView();

        // Create a label to display the Zone and ID
        Label infoLabel = new Label(place.getZone() + "\nID: " + place.getId());
        infoLabel.getStyleClass().add("spot-info-label");

        // Check if the spot is reserved for the current time
        boolean isReserved = isSpotReservedForCurrentTime(place.getId(), reservations);

        // Check if the spot's statut is "taken"
        boolean isTaken = place.getStatut() == StatutPlace.taken;

        if (!isReserved && !isTaken && place.getStatut() == StatutPlace.free) {
            Button reserveButton = createReserveButton(place);
            spot.getStyleClass().add("available-spot");
            carView.setVisible(false);

            // Add the label and reserve button to the spot container
            VBox content = new VBox(5, infoLabel, reserveButton);
            content.setAlignment(Pos.CENTER);
            spotContainer.getChildren().addAll(spot, content);

            availableSpots++;
            setupSpotHoverAnimation(spotContainer);
        } else {
            spot.getStyleClass().add("occupied-spot");
            carView.setVisible(true);

            // Add the label and car image to the spot container
            VBox content = new VBox(5, infoLabel, carView);
            content.setAlignment(Pos.CENTER);
            spotContainer.getChildren().addAll(spot, content);

            if (!animationPlayedMap.containsKey(place.getId())) {
                setupCarEntranceAnimation(carView);
                animationPlayedMap.put(place.getId(), true);
            }
        }

        return spotContainer;
    }

    private Button createReserveButton(PlaceParking place) {
        Button reserveButton = new Button("Reserve");
        reserveButton.getStyleClass().add("reserve-button");

        reserveButton.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), reserveButton);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        reserveButton.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), reserveButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        reserveButton.setOnAction(event -> showReservationDialog(place));
        return reserveButton;
    }

    private void setupSpotHoverAnimation(StackPane spotContainer) {
        spotContainer.setOnMouseEntered(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), spotContainer);
            ft.setToValue(0.8);
            ft.play();
        });

        spotContainer.setOnMouseExited(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), spotContainer);
            ft.setToValue(1.0);
            ft.play();
        });
    }

    private void setupCarEntranceAnimation(ImageView carView) {
        carView.setTranslateX(-100);
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), carView);
        tt.setToX(0);
        tt.play();
    }

    private Rectangle createSpotRectangle() {
        Rectangle spot = new Rectangle(120, 180);
        spot.setArcWidth(20);
        spot.setArcHeight(20);
        spot.setStroke(Color.DARKGRAY);
        spot.setStrokeWidth(2);
        return spot;
    }

    private ImageView createCarImageView() {
        ImageView carView = new ImageView(carImage);
        carView.setFitWidth(100);
        carView.setFitHeight(150);
        carView.setPreserveRatio(true);
        return carView;
    }

    private void showReservationDialog(PlaceParking place) {
        Optional<ReservationDialog.ReservationDetails> result = ReservationDialog.showEnhancedReservationDialog();
        result.ifPresent(details -> confirmReservation(place, details.getStartDateTime(), details));
    }

    private void confirmReservation(PlaceParking place, LocalDateTime dateTime, ReservationDialog.ReservationDetails details) {
        try {
            LocalDateTime expirationTime = dateTime.plusHours(2);
            int utilisateurId = Session.getInstance().getCurrentUser().getId(); // Get current user ID

            // Calculate the total price
            double totalPrice = details.getTotalPrice();

            // Create a new Reservation object with all details
            Reservation reservation = new Reservation(
                    utilisateurId,
                    place.getId(),
                    Timestamp.valueOf(dateTime),
                    Timestamp.valueOf(expirationTime),
                    Reservation.StatutReservation.active,
                    details.getVehicleType(),
                    details.getCarWashType(),
                    details.getNotes(),
                    totalPrice
            );

            // Insert the reservation into the database
            ReservationService reservationService = new ReservationService();
            reservationService.insert(reservation);

            // Send confirmation email to the current user's email
            String to = Session.getInstance().getCurrentUser().getEmail(); // Get current user's email
            String subject = "Confirmation de Réservation";
            String body = "<h1>Confirmation de Réservation</h1>"
                    + "<p>Bonjour,</p>"
                    + "<p>Votre réservation a été confirmée avec succès. Voici les détails :</p>"
                    + "<ul>"
                    + "<li><strong>Numéro de place :</strong> " + place.getId() + "</li>"
                    + "<li><strong>Étage :</strong> " + place.getFloor() + "</li>"
                    + "<li><strong>Zone :</strong> " + place.getZone() + "</li>"
                    + "<li><strong>Date de début :</strong> " + dateTime + "</li>"
                    + "<li><strong>Date de fin :</strong> " + expirationTime + "</li>"
                    + "<li><strong>Type de véhicule :</strong> " + details.getVehicleType() + "</li>"
                    + "<li><strong>Service de lavage :</strong> " + (details.getCarWashType() != null ? details.getCarWashType() : "Aucun") + "</li>"
                    + "<li><strong>Notes :</strong> " + (details.getNotes() != null ? details.getNotes() : "Aucune") + "</li>"
                    + "<li><strong>Coût total :</strong> " + totalPrice + " TND</li>"
                    + "</ul>"
                    + "<p>Merci d'avoir utilisé notre service de parking.</p>";

            emailReservationService.sendReservationConfirmationEmail(to, subject, body);

            // Send SMS notification
            String userPhoneNumber = Session.getInstance().getCurrentUser().getTelephone(); // Get user's phone number
            String smsMessage = "Payment Done: Your payment of TND" + totalPrice + " was successful. Thank you!";
            TwilioService.sendSMS(userPhoneNumber, smsMessage);

            showSuccessAnimation();
            loadParkingSpots();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Reservation Error", "Unable to reserve parking spot: " + e.getMessage());
        }
    }

    private void showSuccessAnimation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Spot Reserved!");
        alert.setContentText("Your parking spot has been reserved successfully.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/parking_lot_css.css").toExternalForm());
        dialogPane.getStyleClass().add("success-dialog");

        FadeTransition ft = new FadeTransition(Duration.millis(1000), dialogPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        alert.showAndWait();
    }

    private void updateStatistics() {
        counterLabel.setText("Available Spots: " + availableSpots);

        if (totalSpots > 0) {
            double occupancyRate = ((totalSpots - availableSpots) / (double) totalSpots) * 100;
            occupancyLabel.setText(String.format("Occupancy: %.1f%%", occupancyRate));
        } else {
            occupancyLabel.setText("Occupancy: 0.0%"); // Handle division by zero
        }
    }

    private void showErrorAlert(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.show();
        });
    }
    private boolean isSpotReservedForCurrentTime(int spotId, List<Reservation> reservations) {
        LocalDateTime now = LocalDateTime.now();

        for (Reservation reservation : reservations) {
            if (reservation.getIdParking() == spotId &&
                    now.isAfter(reservation.getDateReservation().toLocalDateTime()) &&
                    now.isBefore(reservation.getDateExpiration().toLocalDateTime())) {
                return true; // Spot is reserved for the current time
            }
        }

        return false; // Spot is not reserved for the current time
    }
    private void updateParkingSpotStatusBasedOnReservations() {
        try {
            List<Reservation> reservations = new ReservationService().showAll();
            LocalDateTime now = LocalDateTime.now();

            for (Reservation reservation : reservations) {
                if (now.isAfter(reservation.getDateReservation().toLocalDateTime()) &&
                        now.isBefore(reservation.getDateExpiration().toLocalDateTime())) {
                    // Update the parking spot status to "taken"
                    parkingService.updateParkingSpotStatus(reservation.getIdParking(), StatutPlace.taken);
                } else if (now.isAfter(reservation.getDateExpiration().toLocalDateTime())) {
                    // Update the parking spot status to "free" and mark reservation as "expired"
                    parkingService.updateParkingSpotStatus(reservation.getIdParking(), StatutPlace.free);
                    new ReservationService().updateReservationStatus(reservation.getIdReservation(), Reservation.StatutReservation.expired);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating parking spot status based on reservations", e);
        }
    }

    @FXML


    private void handleCancelReservationButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cancel_reservation_dialog.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Cancel Reservation");

            // Get the controller and set the user reservations
            CancelReservationDialogController controller = loader.getController();
            List<Reservation> userReservations = getUserReservations(); // Fetch reservations for the user
            controller.setUserReservations(userReservations); // Set the reservations

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Reservation> getUserReservations() throws SQLException {
        // Get the current user from the session
        Utilisateur currentUser = Session.getInstance().getCurrentUser();

        // Check if a user is logged in
        if (currentUser != null) {
            int userId = currentUser.getId(); // Get the user ID from the session
            return new ReservationService().getReservationsByUserId(userId);
        } else {
            // Handle the case where no user is logged in
            throw new IllegalStateException("No user is currently logged in.");
        }
    }
}