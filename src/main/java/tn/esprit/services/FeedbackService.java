package tn.esprit.services;
import tn.esprit.entites.Feedback;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService {

    private Connection cnx = DataBase.getInstance().getCnx();
    private PreparedStatement ps;

    // 🔹 Ajouter un feedback
    public int insert(Feedback feedback) throws SQLException {
        // Vérifier si l'utilisateur a déjà donné un feedback pour ce shop
        String checkQuery = "SELECT COUNT(*) FROM feedback WHERE utilisateur_id = ? AND shop_id = ?";
        ps = cnx.prepareStatement(checkQuery);
        ps.setInt(1, feedback.getUtilisateurId());//curent user
        ps.setInt(2, feedback.getShopId());//

        ResultSet rs = ps.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            throw new SQLException("Erreur : Vous avez déjà donné un feedback pour ce shop.");
        }

        // Insertion du feedback si l'utilisateur n'a pas encore noté ce shop
        String insertQuery = "INSERT INTO feedback (utilisateur_id, shop_id, rating) VALUES (?, ?, ?)";
        ps = cnx.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, feedback.getUtilisateurId());
        ps.setInt(2, feedback.getShopId());
        ps.setInt(3, feedback.getRating());

        int rowsAffected = ps.executeUpdate();
        ResultSet generatedKeys = ps.getGeneratedKeys();

        if (generatedKeys.next()) {
            feedback.setId(generatedKeys.getInt(1));
            return feedback.getId();
        } else {
            throw new SQLException("Erreur: L'ID du feedback n'a pas été généré.");
        }
    }


    // 🔹 Modifier un feedback
    public int update(Feedback feedback) throws SQLException {
        String req = "UPDATE feedback SET rating = ? WHERE id = ? AND utilisateur_id = ?";
        ps = cnx.prepareStatement(req);
        ps.setInt(1, feedback.getRating());
        ps.setInt(2, feedback.getId());
        ps.setInt(3, feedback.getUtilisateurId());

        return ps.executeUpdate();
    }

    // 🔹 Supprimer un feedback
    public int delete(int id) throws SQLException {
        String req = "DELETE FROM feedback WHERE id = ?";
        ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        return ps.executeUpdate();
    }

    // 🔹 Récupérer tous les feedbacks
    public List<Feedback> showAll() throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String req = "SELECT * FROM feedback";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Feedback feedback = new Feedback(
                    rs.getInt("utilisateur_id"),
                    rs.getInt("shop_id"),
                    rs.getInt("rating")
            );
            feedback.setId(rs.getInt("id"));
            feedback.setDateFeedback(rs.getTimestamp("date_feedback"));

            feedbacks.add(feedback);
        }
        return feedbacks;
    }
}

