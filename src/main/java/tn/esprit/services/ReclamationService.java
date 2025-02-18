package tn.esprit.services;

import tn.esprit.entites.Reclamation;
import tn.esprit.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {
    private Connection connection;

    public ReclamationService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public void addReclamation(Reclamation reclamation) throws SQLException {
        String query = "INSERT INTO reclamation (description, commentaire, nomshop, statut) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, reclamation.getDescription());
        pst.setString(2, reclamation.getCommentaire());
        pst.setString(3, reclamation.getNomshop());
        pst.setString(4, reclamation.getStatut());
        pst.executeUpdate();
    }

    public void updateReclamation(Reclamation reclamation) throws SQLException {
        String query = "UPDATE reclamation SET commentaire = ?, statut = ? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, reclamation.getCommentaire());
        pst.setString(2, reclamation.getStatut());
        pst.setInt(3, reclamation.getId());
        pst.executeUpdate();
    }

    public List<Reclamation> getAllReclamations() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Reclamation reclamation = new Reclamation(
                    rs.getInt("id"),
                    rs.getString("description"),
                    rs.getString("commentaire"),
                    rs.getString("nomshop"),
                    rs.getString("statut")
            );
            reclamations.add(reclamation);
        }
        return reclamations;
    }
}