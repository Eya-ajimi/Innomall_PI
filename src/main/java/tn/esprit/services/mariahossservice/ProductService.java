package tn.esprit.services.mariahossservice;
import tn.esprit.entities.Produit;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService implements CRUD<Produit> {

    private Connection connection;

    public ProductService() {
        this.connection = DataBase.getInstance().getCnx();
    }

    @Override
    public int insert(Produit produit) throws SQLException {
        String query = "INSERT INTO produit (shopId, promotionId, nom, description, stock, prix, photoUrl) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, produit.getShopId());
            if (produit.getPromotionId() != null) {
                pst.setInt(2, produit.getPromotionId());
            } else {
                pst.setNull(2, Types.INTEGER);
            }
            pst.setString(3, produit.getNom());
            pst.setString(4, produit.getDescription());
            pst.setInt(5, produit.getStock());
            pst.setDouble(6, produit.getPrix());
            pst.setString(7, produit.getPhotoUrl());

            return pst.executeUpdate();
        }
    }

    @Override
    public int update(Produit produit) throws SQLException {
        String query = "UPDATE produit SET promotionId = ?, nom = ?, description = ?, stock = ?, prix = ?, photoUrl = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            if (produit.getPromotionId() == null) {
                pst.setNull(1, java.sql.Types.INTEGER);
            } else {
                pst.setInt(1, produit.getPromotionId());
            }
            pst.setString(2, produit.getNom());
            pst.setString(3, produit.getDescription());
            pst.setInt(4, produit.getStock());
            pst.setDouble(5, produit.getPrix());
            pst.setString(6, produit.getPhotoUrl());
            pst.setInt(7, produit.getId());

            return pst.executeUpdate();
        }
    }

    @Override
    public int delete(Produit produit) throws SQLException {
        String query = "DELETE FROM produit WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, produit.getId());
            return pst.executeUpdate();
        }
    }

    @Override
    public List<Produit> showAll() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setShopId(rs.getInt("shopId"));
                produit.setPromotionId(rs.getObject("promotionId") != null ? rs.getInt("promotionId") : null);
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setStock(rs.getInt("stock"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setPhotoUrl(rs.getString("photoUrl"));
                produits.add(produit);
            }
        }
        return produits;
    }


    public Produit getEntityById(int id) throws SQLException {
        String query = "SELECT * FROM produit WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Produit(
                        rs.getInt("id"),
                        rs.getInt("shopId"),
                        rs.getObject("promotionId") != null ? rs.getInt("promotionId") : null,
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("stock"),
                        rs.getFloat("prix"),
                        rs.getString("photoUrl")
                );
            }
        }
        return null;
    }

    public List<Produit> getProductsByShopId(int shopId) throws SQLException {
        String query = "SELECT * FROM produit WHERE shopId = ?";
        List<Produit> produitList = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, shopId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setShopId(rs.getInt("shopId"));
                produit.setPromotionId(rs.getObject("promotionId") != null ? rs.getInt("promotionId") : null);
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setStock(rs.getInt("stock"));
                produit.setPrix(rs.getFloat("prix"));
                produit.setPhotoUrl(rs.getString("photoUrl"));
                produitList.add(produit);
            }
        }

        return produitList;
    }

    public int countProductsByShopId(int shopId) throws SQLException {
        String query = "SELECT COUNT(*) FROM produit WHERE shopId = ?";
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