package tn.esprit.tests;

import tn.esprit.services.ReservationService;
import tn.esprit.utils.DataBase;

import java.sql.*;

public class TestReservation {
    public static void main(String[] args) {
        DataBase db = DataBase.getInstance();
        ReservationService reservationService = new ReservationService();
        int clientId = 2;  // Assume the client with id 4 exists
        int parkingId = 14; // Assume the parking spot with id 3 exists

        System.out.println("Checking if client and parking spot exist...");

        // Check if client exists and display client info
        if (!clientExists(clientId, db.getCnx())) {
            System.out.println("❌ Error: Client with ID " + clientId + " does not exist.");
            return;
        } else {
            displayClientInfo(clientId, db.getCnx()); // Display client info
        }

        // Check if parking exists and if it's free, and display parking info
        String parkingStatus = parkingExistsAndFree(parkingId, db.getCnx());
        if (parkingStatus == null) {
            System.out.println("❌ Error: Parking spot with ID " + parkingId + " does not exist.");
            return;
        } else if (parkingStatus.equals("taken")) {
            System.out.println("❌ Error: Parking spot with ID " + parkingId + " exists but is already taken.");
            return;
        } else {
            displayParkingInfo(parkingId, db.getCnx()); // Display parking info
        }

        System.out.println("✅ Client and parking spot are valid and available.");

        // Attempt to create the reservation
        Timestamp dateReservation = Timestamp.valueOf("2025-02-09 10:00:00");
        Timestamp dateExpiration = Timestamp.valueOf("2025-02-10 10:00:00");

        boolean reservationAdded = reservationService.addReservation(clientId, parkingId, dateReservation, dateExpiration);
        if (reservationAdded) {
            System.out.println("🎉 Reservation added successfully!");
        } else {
            System.out.println("❌ Failed to add reservation.");
        }


    }

    // Method to check if a client exists in the database
    public static boolean clientExists(int clientId, Connection connection) {
        String sql = "SELECT COUNT(*) FROM Client WHERE idClient = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Returns true if count > 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to display client information
    public static void displayClientInfo(int clientId, Connection connection) {
        String sql = "SELECT * FROM Client WHERE idClient = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("\n📋 Client Information:");
                System.out.println("ID: " + resultSet.getInt("idClient"));
                System.out.println("Name: " + resultSet.getString("nom") + " " + resultSet.getString("prenom"));
                System.out.println("Phone: " + resultSet.getString("telephone"));
                System.out.println("Points: " + resultSet.getInt("nombrePoint"));
                System.out.println("Gains: " + resultSet.getInt("nombreGains"));
                System.out.println("Role: " + resultSet.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to check if a parking spot exists and if it's free
    public static String parkingExistsAndFree(int parkingId, Connection connection) {
        String sql = "SELECT statut FROM PlaceParking WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parkingId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("statut"); // Return the status of the parking spot
            } else {
                return null; // Parking spot does not exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to display parking spot information
    public static void displayParkingInfo(int parkingId, Connection connection) {
        String sql = "SELECT * FROM PlaceParking WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parkingId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("\n🅿️ Parking Spot Information:");
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Zone: " + resultSet.getString("zone"));
                System.out.println("Status: " + resultSet.getString("statut"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}