package tn.esprit.gui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.PlaceParking;
import tn.esprit.enums.StatutPlace;
import tn.esprit.services.PlaceParkingService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingLotController implements Initializable {
    private PlaceParkingService parkingService = new PlaceParkingService();
    private Image carImage;
    private Timeline pulseAnimation;
    @FXML private Button viewReservedSpotsButton;
    @FXML private Label counterLabel;
    @FXML private Label occupancyLabel;
    @FXML private VBox parkingArea;
    @FXML private HBox topRow;
    @FXML private HBox bottomRow;
    @FXML private Rectangle passage;
    @FXML private StackPane statsCard1;
    @FXML private StackPane statsCard2;
    @FXML private ComboBox<String> floorComboBox;

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

        initializeAnimations();
        initializeAutoRefresh();
        initializeFloorComboBox();
        loadParkingSpots();
        setupStatsCardAnimation(statsCard1);
        setupStatsCardAnimation(statsCard2);
    }

    private void initializeAnimations() {
        Timeline passageAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(passage.opacityProperty(), 0.7)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(passage.opacityProperty(), 1))
        );
        passageAnimation.setAutoReverse(true);
        passageAnimation.setCycleCount(Timeline.INDEFINITE);
        passageAnimation.play();

        pulseAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(passage.scaleXProperty(), 1)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(passage.scaleXProperty(), 1.02)),
                new KeyFrame(Duration.seconds(1), new KeyValue(passage.scaleXProperty(), 1))
        );
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.play();
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
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> loadParkingSpots()));
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
            String selectedFloor = floorComboBox.getValue();
            List<PlaceParking> parkingSpots = parkingService.showAll();
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

            totalSpots = filteredSpots.size(); // Fix: Use only spots from the selected floor

            int half = filteredSpots.size() / 2;
            for (int i = 0; i < filteredSpots.size(); i++) {
                PlaceParking place = filteredSpots.get(i);
                StackPane spotContainer = createParkingSpot(place);

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

    private StackPane createParkingSpot(PlaceParking place) {
        StackPane spotContainer = new StackPane();
        spotContainer.getStyleClass().add("parking-spot");

        Rectangle spot = createSpotRectangle();
        ImageView carView = createCarImageView();

        // Create a label to display the Zone and ID
        Label infoLabel = new Label( place.getZone() + "\nID: " + place.getId());
        infoLabel.getStyleClass().add("spot-info-label");

        if (place.getStatut() == StatutPlace.free) {
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
        Optional<LocalDateTime> result = ReservationDialog.showReservationDialog();
        result.ifPresent(dateTime -> confirmReservation(place, dateTime));
    }

    private void confirmReservation(PlaceParking place, LocalDateTime dateTime) {
        try {
            LocalDateTime expirationTime = dateTime.plusHours(2);
            int utilisateurId = 1;
            parkingService.reserveSpot(place.getId(), utilisateurId, dateTime, expirationTime);

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
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
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
}