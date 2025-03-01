package tn.esprit.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Reservation;
import tn.esprit.services.ReservationService;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CancelReservationDialogController {

    @FXML private VBox root;
    @FXML private TableView<Reservation> reservationTableView; // Changed from ListView to TableView
    @FXML private TableColumn<Reservation, Integer> spotColumn; // New column for Spot #
    @FXML private TableColumn<Reservation, String> dateReservationColumn; // New column for Date Reservation
    @FXML private TableColumn<Reservation, String> dateExpirationColumn; // New column for Date Expiration
    @FXML private TableColumn<Reservation, String> statusColumn; // New column for Status
    @FXML private Button closeButton;
    @FXML private Button backButton;
    @FXML private Button cancelButton;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private VBox detailsContainer;

    // Details section labels
    @FXML private Label idValueLabel;
    @FXML private Label dateValueLabel;
    @FXML private Label timeValueLabel;
    @FXML private Label locationValueLabel;
    @FXML private Label typeValueLabel;
    @FXML private Label priceValueLabel;
    @FXML private Label notesValueLabel;

    private List<Reservation> userReservations;
    private FilteredList<Reservation> filteredReservations;

    @FXML
    public void initialize() {
        // Set up close button action if it exists
        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            });
        }

        // Set up back button
        if (backButton != null) {
            backButton.setOnAction(event -> {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            });
        }

        // Initialize status filter dropdown
        if (statusFilter != null) {
            statusFilter.setItems(FXCollections.observableArrayList(
                    "All", "reserved", "expired", "canceled"
            ));
            statusFilter.setValue("All");

            // Add listener for filter changes
            statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
                applyFilters();
            });
        }

        // Set up search functionality
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                applyFilters();
            });
        }

        // Set up selection change listener for reservation details
        reservationTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        showReservationDetails(newValue);
                    } else {
                        hideReservationDetails();
                    }
                });

        // Initially hide details container
        if (detailsContainer != null) {
            detailsContainer.setVisible(false);
            detailsContainer.setManaged(false);
        }

        // Initialize table columns
        initializeTableColumns();
    }

    private void initializeTableColumns() {
        // Bind columns to Reservation fields
        spotColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdParking()).asObject());
        dateReservationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateReservation().toString()));
        dateExpirationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateExpiration().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut().toString()));
    }

    private void showReservationDetails(Reservation reservation) {
        if (detailsContainer != null) {
            // Set Reservation ID
            idValueLabel.setText(String.valueOf(reservation.getIdReservation()));

            // Set Date of Reservation
            String dateReservation = reservation.getDateReservation().toString(); // Default format
            dateValueLabel.setText(dateReservation);

            // Set Date of Expiration
            String dateExpiration = reservation.getDateExpiration().toString(); // Default format
            timeValueLabel.setText(dateExpiration); // Reusing the timeValueLabel for expiration date

            // Set Car Wash Type
            locationValueLabel.setText(reservation.getCarWashType() != null ? reservation.getCarWashType() : "N/A");

            // Set Vehicle Type
            typeValueLabel.setText(reservation.getVehicleType() != null ? reservation.getVehicleType() : "N/A");

            // Set Notes
            notesValueLabel.setText(reservation.getNotes() != null ? reservation.getNotes() : "N/A");

            // Set Price
            priceValueLabel.setText(String.valueOf(reservation.getPrice()) + " TND");

            // Show details container
            detailsContainer.setVisible(true);
            detailsContainer.setManaged(true);
        }
    }

    private void hideReservationDetails() {
        if (detailsContainer != null) {
            detailsContainer.setVisible(false);
            detailsContainer.setManaged(false);
        }
    }

    private void applyFilters() {
        if (filteredReservations == null) return;

        String searchText = searchField.getText();
        String statusValue = statusFilter.getValue();

        filteredReservations.setPredicate(reservation -> {
            // Search filter
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();

                return String.valueOf(reservation.getIdReservation()).contains(lowerCaseFilter) ||
                        (reservation.getDateReservation() != null &&
                                reservation.getDateReservation().toString().toLowerCase().contains(lowerCaseFilter)) ||
                        (reservation.toString().toLowerCase().contains(lowerCaseFilter));
            }

            return true;
        });
    }

    public void setUserReservations(List<Reservation> userReservations) {
        // Initialize the userReservations list
        this.userReservations = userReservations;

        // Filter reservations by user ID (hardcoded to 1) and status "reserved"
        List<Reservation> filteredList = userReservations.stream()
                .filter(reservation -> reservation.getIdUtilisateur() == 1) // Filter by user ID
                .filter(reservation -> reservation.getStatut().toString().equals("reserved")) // Filter by status
                .collect(Collectors.toList());

        // Create a FilteredList with the filtered reservations
        this.filteredReservations = new FilteredList<>(FXCollections.observableArrayList(filteredList));
        reservationTableView.setItems(this.filteredReservations);
    }

    @FXML
    private void handleCancelReservationAction() {
        Reservation selectedReservation = reservationTableView.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            // Check if the reservation is already canceled or expired
            if (!selectedReservation.getStatut().toString().equals("reserved")) {
                if (statusLabel != null) {
                    statusLabel.setText("Cannot cancel a reservation that is already canceled or expired.");
                    statusLabel.getStyleClass().clear();
                    statusLabel.getStyleClass().addAll("status-message", "error");
                }
                return;
            }

            try {
                ReservationService reservationService = new ReservationService();
                reservationService.cancelReservation(selectedReservation.getIdReservation());

                // Update the list
                userReservations.remove(selectedReservation);

                // Recreate the filteredReservations list
                List<Reservation> filteredList = userReservations.stream()
                        .filter(reservation -> reservation.getIdUtilisateur() == 1) // Filter by user ID
                        .filter(reservation -> reservation.getStatut().toString().equals("reserved")) // Filter by status
                        .collect(Collectors.toList());

                this.filteredReservations = new FilteredList<>(FXCollections.observableArrayList(filteredList));
                reservationTableView.setItems(this.filteredReservations);

                // Apply current filters
                applyFilters();

                // Hide details panel
                hideReservationDetails();

                // Show success message
                if (statusLabel != null) {
                    statusLabel.setText("Reservation cancelled successfully");
                    statusLabel.getStyleClass().clear();
                    statusLabel.getStyleClass().addAll("status-message", "success");
                }
            } catch (SQLException e) {
                e.printStackTrace();

                // Show error message
                if (statusLabel != null) {
                    statusLabel.setText("Error cancelling reservation: " + e.getMessage());
                    statusLabel.getStyleClass().clear();
                    statusLabel.getStyleClass().addAll("status-message", "error");
                }
            }
        } else {
            // Show selection required message
            if (statusLabel != null) {
                statusLabel.setText("Please select a reservation to cancel");
                statusLabel.getStyleClass().clear();
                statusLabel.getStyleClass().addAll("status-message", "error");
            }
        }
    }
}