package tn.esprit.gui;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Reservation;
import tn.esprit.services.ReservationService;

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

        // Load data
        loadReservations();

        // Setup search functionality
        setupSearch();

        // Setup status filter
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
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return String.valueOf(reservation.getIdParking()).contains(lowerCaseFilter) ||
                        reservation.getDateReservation().toString().toLowerCase().contains(lowerCaseFilter) ||
                        reservation.getStatut().toString().toLowerCase().contains(lowerCaseFilter);
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