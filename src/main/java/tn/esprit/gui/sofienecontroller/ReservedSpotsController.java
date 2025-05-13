package tn.esprit.gui.sofienecontroller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Reservation;
import tn.esprit.services.sofieneservice.ReservationService;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.Timestamp;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ReservedSpotsController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    private ReservationService reservationService = new ReservationService();
    private FilteredList<Reservation> filteredReservations;
    public void setReservations(List<Reservation> reservations) {
        filteredReservations = new FilteredList<>(FXCollections.observableArrayList(reservations));
        reservationTable.setItems(filteredReservations);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize status filter
        statusFilter.setItems(FXCollections.observableArrayList(
                "All", "active", "expired", "cancelled"
        ));
        statusFilter.setValue("All");


        initializeTableColumns();
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
    private void initializeTableColumns() {
        // Clear existing columns
        reservationTable.getColumns().clear();

        // Spot #
        TableColumn<Reservation, Integer> spotColumn = new TableColumn<>("Spot #");
        spotColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdParking()).asObject());

        // Reserved On
        TableColumn<Reservation, String> reservedColumn = new TableColumn<>("Reserved On");
        reservedColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = cellData.getValue().getDateReservation();
            return new SimpleStringProperty(timestamp != null ? timestamp.toString() : "");
        });

        // Expires On
        TableColumn<Reservation, String> expiresColumn = new TableColumn<>("Expires On");
        expiresColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = cellData.getValue().getDateExpiration();
            return new SimpleStringProperty(timestamp != null ? timestamp.toString() : "");
        });

        // Status
        TableColumn<Reservation, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatut().toString()));

        // Vehicle Type
        TableColumn<Reservation, String> vehicleColumn = new TableColumn<>("Vehicle Type");
        vehicleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getVehicleType()));

        // Car Wash Type
        TableColumn<Reservation, String> washColumn = new TableColumn<>("Car Wash");
        washColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCarWashType()));

        // Price
        TableColumn<Reservation, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f TND", cellData.getValue().getPrice())));

        // Add all columns to the table
        reservationTable.getColumns().addAll(
                spotColumn,
                reservedColumn,
                expiresColumn,
                statusColumn,
                vehicleColumn,
                washColumn,
                priceColumn
        );

        // Set column resize policy
        reservationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}