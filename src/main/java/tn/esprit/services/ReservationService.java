package tn.esprit.services;

import tn.esprit.entities.Reservation;
import tn.esprit.entities.Reservation.StatutReservation;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements CRUD<Reservation> {

    @Override
    public int insert(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO Reservation (idUtilisateur, idParking, dateReservation, dateExpiration, statut) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DataBase.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, reservation.getIdUtilisateur());
            statement.setInt(2, reservation.getIdParking());
            statement.setTimestamp(3, reservation.getDateReservation());
            statement.setTimestamp(4, reservation.getDateExpiration());
            statement.setString(5, reservation.getStatut().toString());  // Store the enum as a string

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reservation.setIdReservation(generatedKeys.getInt(1));
                }
                updateParkingStatus(reservation.getIdParking(), "taken");
                return rowsInserted;
            }
        }
        return 0;
    }

    @Override
    public int update(Reservation reservation) throws SQLException {
        String sql = "UPDATE Reservation SET idUtilisateur = ?, idParking = ?, dateReservation = ?, dateExpiration = ?, statut = ? WHERE idReservation = ?";
        try (Connection connection = DataBase.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reservation.getIdUtilisateur());
            statement.setInt(2, reservation.getIdParking());
            statement.setTimestamp(3, reservation.getDateReservation());
            statement.setTimestamp(4, reservation.getDateExpiration());
            statement.setString(5, reservation.getStatut().toString());
            statement.setInt(6, reservation.getIdReservation());

            return statement.executeUpdate();
        }
    }

    @Override
    public int delete(Reservation reservation) throws SQLException {
        String sql = "DELETE FROM Reservation WHERE idReservation = ?";
        try (Connection connection = DataBase.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reservation.getIdReservation());

            // Update parking status to 'free'
            updateParkingStatus(reservation.getIdParking(), "free");

            return statement.executeUpdate();
        }
    }

    @Override
    public List<Reservation> showAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM Reservation";
        try (Connection connection = DataBase.getInstance().getCnx();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Reservation reservation = new Reservation(
                        resultSet.getInt("idUtilisateur"),
                        resultSet.getInt("idParking"),
                        resultSet.getTimestamp("dateReservation"),
                        resultSet.getTimestamp("dateExpiration"),
                        StatutReservation.valueOf(resultSet.getString("statut"))
                );
                reservation.setIdReservation(resultSet.getInt("idReservation"));
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    // Add reservation method
    public boolean addReservation(int utilisateurId, int parkingId, Timestamp dateReservation, Timestamp dateExpiration) {
        Reservation reservation = new Reservation(utilisateurId, parkingId, dateReservation, dateExpiration, StatutReservation.reserved);
        try {
            return insert(reservation) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Private method to update parking status
    private void updateParkingStatus(int parkingId, String status) {
        String sql = "UPDATE PlaceParking SET statut = ? WHERE id = ?";
        try (Connection connection = DataBase.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, parkingId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
