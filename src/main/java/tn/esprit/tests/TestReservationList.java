package tn.esprit.tests;

import tn.esprit.entities.Reservation;
import tn.esprit.services.ReservationService;
import tn.esprit.utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TestReservationList {
    public static void main(String[] args) {
        DataBase db = DataBase.getInstance();
        ReservationService reservationService = new ReservationService();

        System.out.println("Fetching all reservations...");

        try {
            List<Reservation> reservations = reservationService.showAll();

            if (reservations.isEmpty()) {
                System.out.println("No reservations found.");
            } else {
                System.out.println("\n📜 List of Reservations:");
                for (Reservation reservation : reservations) {
                    displayReservationInfo(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public static void displayReservationInfo(Reservation reservation) {
        System.out.println("\n📋 Reservation Information:");
        System.out.println("Reservation ID: " + reservation.getIdReservation());
        System.out.println("Client ID: " + reservation.getIdUtilisateur());
        System.out.println("Parking Spot ID: " + reservation.getIdParking());
        System.out.println("Reservation Date: " + reservation.getDateReservation());
        System.out.println("Expiration Date: " + reservation.getDateExpiration());
        System.out.println("Status: " + reservation.getStatut());
        System.out.println("───────────────────────────────────────────────");
    }
}