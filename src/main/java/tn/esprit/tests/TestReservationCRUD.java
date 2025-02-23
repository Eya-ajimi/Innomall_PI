package tn.esprit.tests;

import tn.esprit.entities.Reservation;
import tn.esprit.entities.Reservation.StatutReservation;
import tn.esprit.services.ReservationService;

import java.sql.Timestamp;
import java.util.List;

public class TestReservationCRUD {

    public static void main(String[] args) {
        // Create an instance of ReservationService
        ReservationService reservationService = new ReservationService();

        // Test data
        int utilisateurId = 1;
        int parkingId = 1;
        Timestamp dateReservation = Timestamp.valueOf("2023-10-25 10:00:00");
        Timestamp dateExpiration = Timestamp.valueOf("2023-10-25 12:00:00");

        // Test insert
        System.out.println("Testing insert...");
        Reservation reservation = new Reservation(utilisateurId, parkingId, dateReservation, dateExpiration, StatutReservation.reserved);
        try {
            int rowsInserted = reservationService.insert(reservation);
            System.out.println("Rows inserted: " + rowsInserted);
            System.out.println("Reservation ID: " + reservation.getIdReservation());
        } catch (Exception e) {
            System.err.println("Error during insert: " + e.getMessage());
        }

        // Test showAll
        System.out.println("\nTesting showAll...");
        try {
            List<Reservation> reservations = reservationService.showAll();
            for (Reservation res : reservations) {
                System.out.println(res);
            }
        } catch (Exception e) {
            System.err.println("Error during showAll: " + e.getMessage());
        }

        // Test update
        System.out.println("\nTesting update...");
        if (reservation.getIdReservation() > 0) {
            reservation.setStatut(StatutReservation.canceled);
            try {
                int rowsUpdated = reservationService.update(reservation);
                System.out.println("Rows updated: " + rowsUpdated);
            } catch (Exception e) {
                System.err.println("Error during update: " + e.getMessage());
            }
        }

        // Test delete
        System.out.println("\nTesting delete...");
        if (reservation.getIdReservation() > 0) {
            try {
                int rowsDeleted = reservationService.delete(reservation);
                System.out.println("Rows deleted: " + rowsDeleted);
            } catch (Exception e) {
                System.err.println("Error during delete: " + e.getMessage());
            }
        }

        // Test addReservation
        System.out.println("\nTesting addReservation...");
        boolean isAdded = reservationService.addReservation(utilisateurId, parkingId, dateReservation, dateExpiration);
        System.out.println("Reservation added: " + isAdded);
    }
}