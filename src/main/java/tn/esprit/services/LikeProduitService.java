package tn.esprit.services;

import tn.esprit.entites.LikeProduit;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeProduitService {

    private Connection cnx = DataBase.getInstance().getCnx();
    private PreparedStatement ps;

    // 1️⃣ Ajouter un like (si non existant)
    public int insert(LikeProduit likeProduit) throws SQLException {
        String checkQuery = "SELECT id FROM LikeProduit WHERE utilisateurId = ? AND produitId = ?";
        ps = cnx.prepareStatement(checkQuery);
        ps.setInt(1, likeProduit.getUtilisateurId());
        ps.setInt(2, likeProduit.getProduitId());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            throw new SQLException("Erreur: L'utilisateur a déjà liké ce produit !");
        }

        // Si pas encore liké, on ajoute
        String req = "INSERT INTO LikeProduit (utilisateurId, produitId) VALUES (?, ?)";
        ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, likeProduit.getUtilisateurId());
        ps.setInt(2, likeProduit.getProduitId());

        int rowsAffected = ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Erreur: Impossible de récupérer l'ID du like inséré.");
        }
    }

    // 2️⃣ Supprimer un like
    public int delete(int utilisateurId, int produitId) throws SQLException {
        String req = "DELETE FROM LikeProduit WHERE utilisateurId = ? AND produitId = ?";
        ps = cnx.prepareStatement(req);
        ps.setInt(1, utilisateurId);
        ps.setInt(2, produitId);

        int rowsAffected = ps.executeUpdate();

        if (rowsAffected == 0) {
            throw new SQLException("Erreur: Aucun like trouvé pour cet utilisateur et ce produit.");
        }

        return rowsAffected;
    }

    // 3️⃣ Récupérer tous les likes d'un produit
    public List<Integer> getLikesByProduit(int produitId) throws SQLException {
        List<Integer> utilisateurs = new ArrayList<>();
        String req = "SELECT utilisateurId FROM LikeProduit WHERE produitId = ?";
        ps = cnx.prepareStatement(req);
        ps.setInt(1, produitId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            utilisateurs.add(rs.getInt("utilisateurId"));
        }

        return utilisateurs;
    }
}
