package tn.esprit.services;

import tn.esprit.entities.LikedProduct;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikedProductService {

    private Connection connection;

    public LikedProductService() {
        this.connection = DataBase.getInstance().getCnx();
    }

    public List<LikedProduct> showAll() throws SQLException {
        List<LikedProduct> likedproducts = new ArrayList<>();
        String query = "SELECT * FROM likedproducts";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                LikedProduct likedProduct = new LikedProduct(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("product_id"),
                        rs.getTimestamp("date")
                );
                likedproducts.add(likedProduct);
            }
        }
        return likedproducts;
    }

    public LikedProduct getEntityById(int id) throws SQLException {
        String query = "SELECT * FROM likedproducts WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new LikedProduct(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("product_id"),
                        rs.getTimestamp("date")
                );
            }
        }
        return null;
    }

    public int countLikesByProductId(int productId) throws SQLException {
        String query = "SELECT COUNT(*) FROM likedproducts WHERE product_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, productId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Integer> getTopLikedProductsByShopId(int shopId) throws SQLException {
        String query = """
        SELECT p.id 
        FROM likedproducts lp
        JOIN produit p ON lp.product_id = p.id
        WHERE p.shopId = ?  
        GROUP BY p.id
        ORDER BY COUNT(lp.id) DESC
        LIMIT 3
    """;

        List<Integer> topProducts = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, shopId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                topProducts.add(rs.getInt("id"));
            }
        }
        return topProducts;
    }

    public int countTotalLikesByShopId(int shopId) throws SQLException {
        String query = """
        SELECT COUNT(lp.id) 
        FROM likedproducts lp
        JOIN produit p ON lp.product_id = p.id
        WHERE p.shopId = ?   -- FIXED: Replaced user_id with shopId
    """;

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, shopId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
