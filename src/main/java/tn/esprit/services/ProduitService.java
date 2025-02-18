package tn.esprit.services;


import tn.esprit.entites.Produit;
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
                produit.setImageUrl(rs.getString("image_url"));
                produit.setDateAjout(rs.getTimestamp("date_ajout").toLocalDateTime());

                produits.add(produit);
            }
        }

        return produits;
    }
}