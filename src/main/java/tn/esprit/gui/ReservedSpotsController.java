package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import tn.esprit.entities.Reservation;
import tn.esprit.services.ReservationService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReservedSpotsController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;

    private ReservationService reservationService = new ReservationService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            reservationTable.getItems().setAll(reservationService.showAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void refreshData() {
        try {
            reservationTable.getItems().setAll(reservationService.showAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleCloseButtonAction() {
        Stage stage = (Stage) reservationTable.getScene().getWindow();
        stage.close();
    }
}