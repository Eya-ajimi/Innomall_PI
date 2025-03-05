package tn.esprit.services.sofieneservice;

import tn.esprit.entities.enums.StatutReservation;
import tn.esprit.entities.PlaceParking;
import tn.esprit.utils.DataBase;
import tn.esprit.entities.enums.StatutPlace;
import tn.esprit.utils.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlaceParkingService implements CRUD<PlaceParking> {
    private final Connection cnx = DataBase.getInstance().getCnx();

    @Override
    public int insert(PlaceParking place) throws SQLException {
        String query = "INSERT INTO PlaceParking (zone, statut, floor) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, place.getZone());
            ps.setString(2, place.getStatut().name()); // Enum to String
            ps.setString(3, place.getFloor()); // Add floor
            return ps.executeUpdate();
        }
    }

    @Override
    public int update(PlaceParking place) throws SQLException {
        String query = "UPDATE PlaceParking SET zone = ?, statut = ?, floor = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, place.getZone());
            ps.setString(2, place.getStatut().name()); // Enum to String
            ps.setString(3, place.getFloor()); // Add floor
            ps.setInt(4, place.getId());
            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(PlaceParking place) throws SQLException {
        String query = "DELETE FROM PlaceParking WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, place.getId());
            return ps.executeUpdate();
        }
    }

    // In PlaceParkingService
    @Override
    public List<PlaceParking> showAll() throws SQLException {
        List<PlaceParking> places = new ArrayList<>();
        String query = "SELECT * FROM PlaceParking";

        try (Connection connection = DataBase.getInstance().getCnx();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                places.add(new PlaceParking(
                        rs.getInt("id"),
                        rs.getString("zone"),
                        StatutPlace.valueOf(rs.getString("statut")),
                        rs.getString("floor")
                ));
            }
        }
        return places;
    }
    public void updateParkingSpotStatus(int parkingId, StatutPlace status) throws SQLException {
        String query = "UPDATE PlaceParking SET statut = ? WHERE id = ?";
        try (Connection connection = DataBase.getInstance().getCnx();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, status.name());
            ps.setInt(2, parkingId);
            ps.executeUpdate();
        }
    }
    public void reserveSpot(int parkingId, LocalDateTime dateReservation, LocalDateTime dateExpiration) throws SQLException {
        int utilisateurId = Session.getInstance().getCurrentUser().getId(); // Get current user ID
        String insertQuery = "INSERT INTO Reservation (idUtilisateur, idParking, dateReservation, dateExpiration, statut) VALUES (?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE PlaceParking SET statut = ? WHERE id = ? AND statut = ?";

        try {
            cnx.setAutoCommit(false);

            // Check if the parking spot is still available
            String checkQuery = "SELECT statut FROM PlaceParking WHERE id = ?";
            try (PreparedStatement checkStmt = cnx.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, parkingId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && !rs.getString("statut").equals(StatutPlace.free.name())) {
                    throw new SQLException("Parking spot is already reserved or occupied.");
                }
            }

            // Insert into Reservation table
            try (PreparedStatement insertStmt = cnx.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, utilisateurId);
                insertStmt.setInt(2, parkingId);
                insertStmt.setTimestamp(3, Timestamp.valueOf(dateReservation));
                insertStmt.setTimestamp(4, Timestamp.valueOf(dateExpiration));
                insertStmt.setString(5, StatutReservation.reserved.name());
                insertStmt.executeUpdate();
            }

            // Update PlaceParking statut to reserved
            try (PreparedStatement updateStmt = cnx.prepareStatement(updateQuery)) {
                updateStmt.setString(1, StatutPlace.taken.name());
                updateStmt.setInt(2, parkingId);
                updateStmt.setString(3, StatutPlace.free.name()); // Ensure the spot is free before updating
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("Failed to update parking spot status.");
                }
            }

            cnx.commit();
        } catch (SQLException e) {
            cnx.rollback();
            throw e;
        } finally {
            cnx.setAutoCommit(true);
        }
    }
}