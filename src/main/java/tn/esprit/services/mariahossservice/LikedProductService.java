package tn.esprit.services.mariahossservice;

import tn.esprit.entities.LikeProduit;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikedProductService {

    private Connection connection;

    public LikedProductService() {
        this.connection = DataBase.getInstance().getCnx();
    }


    public List<LikeProduit> showAll() throws SQLException {
        List<LikeProduit> likeProduits = new ArrayList<>();
        String query = "SELECT * FROM likeProduit";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                LikeProduit likeProduit = new LikeProduit(
                        rs.getInt("id"),
                        rs.getInt("utilisateurId"),
                        rs.getInt("produitId"),
                        rs.getTimestamp("date_like")
                );
                likeProduits.add(likeProduit);
            }
        }
        return likeProduits;
    }

    public LikeProduit getEntityById(int id) throws SQLException {
        String query = "SELECT * FROM likeProduit) WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new LikeProduit(
                        rs.getInt("id"),
                        rs.getInt("utilisateurId"),
                        rs.getInt("produitId"),
                        rs.getTimestamp("date_like")
                );
            }
        }
        return null;
    }

    public int countLikesByProductId(int productId) throws SQLException {
        String query = "SELECT COUNT(*) FROM likeProduit WHERE produitId = ?";

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
        FROM likeProduit lp
        JOIN produit p ON lp.produitId = p.id
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
        FROM likeProduit lp
        JOIN produit p ON lp.produitId = p.id
        WHERE p.shopId = ?
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
