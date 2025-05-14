package tn.esprit.services.sofieneservice;

import tn.esprit.entities.ParkingAssignment;
import tn.esprit.utils.DataBase;
import java.sql.*;

public class ParkingAssignmentService {
    public ParkingAssignment findLatestAssignment(String phoneNumber) throws SQLException {
        String sql = "SELECT * FROM parking_assignments " +
                "WHERE phone_number = ? " +
                "ORDER BY scanned_at DESC LIMIT 1";

        try (Connection connection = DataBase.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phoneNumber);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new ParkingAssignment(
                        rs.getInt("id"),
                        rs.getInt("placeparking_id"),
                        rs.getString("phone_number"),
                        rs.getTimestamp("scanned_at")
                );
            }
            return null;
        }
    }
    public String getParkingDuration(ParkingAssignment assignment) {
        if (assignment == null || assignment.getScannedAt() == null) {
            return "";
        }

        long millis = System.currentTimeMillis() - assignment.getScannedAt().getTime();
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        return String.format("%d hours %d minutes", hours, minutes);
    }

}