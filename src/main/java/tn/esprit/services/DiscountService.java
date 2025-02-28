package tn.esprit.services;

import tn.esprit.entities.Discount;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountService implements CRUD<Discount> {
    private Connection connection;

    public DiscountService() {
        this.connection = DataBase.getInstance().getCnx();
    }

    @Override
    public int insert(Discount discount) throws SQLException {
        String query = "INSERT INTO discount (shop_id, discount_percentage, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, discount.getShopId());
            pst.setFloat(2, discount.getDiscountPercentage());
            pst.setDate(3, new java.sql.Date(discount.getStartDate().getTime()));
            pst.setDate(4, new java.sql.Date(discount.getEndDate().getTime()));
            return pst.executeUpdate();
        }
    }

    @Override
    public int update(Discount discount) throws SQLException {
        String query = "UPDATE discount SET shop_id = ?, discount_percentage = ?, start_date = ?, end_date = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, discount.getShopId());
            pst.setFloat(2, discount.getDiscountPercentage());
            pst.setDate(3, discount.getStartDate() != null ? new java.sql.Date(discount.getStartDate().getTime()) : null);
            pst.setDate(4, discount.getEndDate() != null ? new java.sql.Date(discount.getEndDate().getTime()) : null);
            pst.setInt(5, discount.getId());

            int affectedRows = pst.executeUpdate();
            if (!connection.getAutoCommit()) {
                connection.commit(); // Commit only if auto-commit is disabled
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            throw e; // Rethrow the exception
        }
    }


    @Override
    public int delete(Discount discount) throws SQLException {
        String query = "DELETE FROM discount WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, discount.getId());
            return pst.executeUpdate();
        }
    }

    @Override
    public List<Discount> showAll() throws SQLException {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM discount";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Discount discount = new Discount(
                        rs.getInt("id"),
                        rs.getInt("shop_id"),
                        rs.getFloat("discount_percentage"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date")
                );
                discounts.add(discount);
            }
        }
        return discounts;
    }


    public Discount getEntityById(int id) throws SQLException {
        String query = "SELECT * FROM discount WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Discount(
                        rs.getInt("id"),
                        rs.getInt("shop_id"),
                        rs.getFloat("discount_percentage"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date")
                );
            }
        }
        return null;
    }

    public List<Discount> getDiscountByShopId(int shopId) throws SQLException {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM discount WHERE shop_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, shopId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Discount discount = new Discount(
                        rs.getInt("id"),
                        rs.getInt("shop_id"),
                        rs.getFloat("discount_percentage"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date")
                );
                discounts.add(discount);
            }
        }
        return discounts;
    }
}
