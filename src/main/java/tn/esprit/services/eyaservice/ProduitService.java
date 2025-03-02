package tn.esprit.services.eyaservice;


import tn.esprit.entities.Produit;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService {

    private Connection cnx = DataBase.getInstance().getCnx();

    // Method to get products by shopId
    public List<Produit> getProduitsByShopId(int shopId) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM Produit WHERE shopId = ?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setShopId(rs.getInt("shopId"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setPromotionId(rs.getInt("promotionId"));
                produit.setStock(rs.getInt("stock"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setImage_url(rs.getString("photoUrl"));
               // produit.setDateAjout(rs.getTimestamp("date_ajout").toLocalDateTime());

                produits.add(produit);
            }
        }

        return produits;
    }


    public List<Produit> getProduitsByShopIdAndFilters(int shopId, String searchQuery, double minPrice, double maxPrice) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit WHERE shopId = ? AND nom LIKE ? AND prix BETWEEN ? AND ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, shopId);
            ps.setString(2, "%" + searchQuery + "%");
            ps.setDouble(3, minPrice);
            ps.setDouble(4, maxPrice);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setDescription(rs.getString("description"));
                produits.add(produit);
            }
        }
        return produits;

    }
}