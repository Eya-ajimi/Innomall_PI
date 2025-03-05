package tn.esprit.gui.sofienecontroller;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Reservation;
import tn.esprit.services.sofieneservice.ReservationService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReservedSpotsController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    private ReservationService reservationService = new ReservationService();
    private FilteredList<Reservation> filteredReservations;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize status filter
        statusFilter.setItems(FXCollections.observableArrayList(
                "All", "reserved", "expired", "canceled"
        ));
        statusFilter.setValue("All");

        loadReservations();

        setupSearch();

        setupStatusFilter();
    }

    private void loadReservations() {
        try {
            filteredReservations = new FilteredList<>(
                    FXCollections.observableArrayList(reservationService.showAll())
            );
            reservationTable.setItems(filteredReservations);
        } catch (SQLException e) {
            showError("Error loading reservations", e.getMessage());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredReservations.setPredicate(reservation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all reservations if the search field is empty
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Check all fields for a match
                return String.valueOf(reservation.getIdParking()).contains(lowerCaseFilter) || // Spot #
                        reservation.getDateReservation().toString().toLowerCase().contains(lowerCaseFilter) || // Reserved On
                        reservation.getDateExpiration().toString().toLowerCase().contains(lowerCaseFilter) || // Expires On
                        reservation.getStatut().toString().toLowerCase().contains(lowerCaseFilter) || // Status
                        (reservation.getVehicleType() != null && reservation.getVehicleType().toLowerCase().contains(lowerCaseFilter)) || // Vehicle Type
                        (reservation.getCarWashType() != null && reservation.getCarWashType().toLowerCase().contains(lowerCaseFilter)) || // Car Wash Type
                        (reservation.getNotes() != null && reservation.getNotes().toLowerCase().contains(lowerCaseFilter)) || // Notes
                        String.valueOf(reservation.getPrice()).contains(lowerCaseFilter); // Price
            });
        });
    }

    private void setupStatusFilter() {
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredReservations.setPredicate(reservation -> {
                if (newValue == null || newValue.equals("All")) {
                    return true;
                }
                return reservation.getStatut().toString().equals(newValue);
            });
        });
    }

    @FXML
    private void handleRefreshAction() {
        loadReservations();
    }

    @FXML
    private void handleCloseButtonAction() {
        Stage stage = (Stage) reservationTable.getScene().getWindow();
        stage.close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}