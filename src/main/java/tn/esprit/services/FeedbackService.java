package tn.esprit.services;

import tn.esprit.entities.Feedback;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class FeedbackService {
    private Connection connection;

    public FeedbackService() {
        this.connection = DataBase.getInstance().getCnx();
    }

    public int insert(Feedback feedback) throws SQLException {
        String query = "INSERT INTO feedback (client_id, shop_owner_id, rate) VALUES (?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, feedback.getClientId());
            pst.setInt(2, feedback.getShopownerId());
            pst.setInt(3, feedback.getRating());
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return affectedRows;
        }
    }

    public Map<Integer, Double> getRatingDistribution() throws SQLException {
        String query = "SELECT rating, COUNT(*) AS count FROM feedback GROUP BY rating;";
        Map<Integer, Double> ratingDistribution = new HashMap<>();
        int totalCount = 0;

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int rating = rs.getInt("rating");
                int count = rs.getInt("count");
                ratingDistribution.put(rating, (double) count);
                totalCount += count;
            }
        }

        if (totalCount > 0) {
            for (Map.Entry<Integer, Double> entry : ratingDistribution.entrySet()) {
                ratingDistribution.put(entry.getKey(), (entry.getValue() / totalCount) * 100);
            }
        }

        return ratingDistribution;
    }
    public Double getAverageRatingByShopId(int shopId) throws SQLException {
        String query = "SELECT AVG(rating) AS average FROM feedback WHERE shop_owner_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, shopId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Double avg = rs.getObject("average", Double.class);
                    return (avg != null) ? avg : null; // Return null if no ratings exist
                }
            }
        }
        return null;
    }



}
