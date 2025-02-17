package tn.esprit.services;

import tn.esprit.entites.SousCommentaire;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SousCommentaireService implements CRUD<SousCommentaire> {

    private Connection cnx = DataBase.getInstance().getCnx();
    private Statement st;
    private PreparedStatement ps;

    @Override
    public int insert(SousCommentaire sousCommentaire) throws SQLException {
        String req = "INSERT INTO `sous_commentaires`(`contenu`, `commentaire_id`, `utilisateur_id`) VALUES (?, ?, ?)";
        ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, sousCommentaire.getContenu());
        ps.setInt(2, sousCommentaire.getCommentaireId());
        ps.setInt(3, sousCommentaire.getUtilisateurId());

        int rowsAffected = ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            sousCommentaire.setId(generatedKeys.getInt(1)); // Met à jour l'ID de l'objet sousCommentaire
        } else {
            throw new SQLException("Erreur: Impossible de récupérer l'ID du sous-commentaire inséré.");
        }

        // Vérifier si l'auteur du post est celui qui répond au commentaire
        String getPostAuthorQuery = "SELECT p.utilisateur_id AS auteur_post, c.utilisateur_id AS auteur_commentaire " +
                "FROM commentaires c " +
                "JOIN postes p ON c.post_id = p.id " +
                "WHERE c.id = ?";
        PreparedStatement psCheck = cnx.prepareStatement(getPostAuthorQuery);
        psCheck.setInt(1, sousCommentaire.getCommentaireId());
        ResultSet rs = psCheck.executeQuery();

        if (rs.next()) {
            int auteurPost = rs.getInt("auteur_post");
            int auteurCommentaire = rs.getInt("auteur_commentaire");

            // Si l'auteur du post répond au commentaire, donner 10 points à l’auteur du commentaire
            if (auteurPost == sousCommentaire.getUtilisateurId()) {
                String updatePointsQuery = "UPDATE utilisateur SET points = points + 10 WHERE id = ?";
                PreparedStatement psUpdate = cnx.prepareStatement(updatePointsQuery);
                psUpdate.setInt(1, auteurCommentaire);
                psUpdate.executeUpdate();
                System.out.println("10 points ajoutés à l'utilisateur ID " + auteurCommentaire);
            }
        }

        return rowsAffected;
    }



    @Override
    public int update(SousCommentaire sousCommentaire) throws SQLException {
        // Vérifier si le sous-commentaire existe
        String checkExist = "SELECT id FROM sous_commentaires WHERE id = ?";
        ps = cnx.prepareStatement(checkExist);
        ps.setInt(1, sousCommentaire.getId());
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            throw new SQLException("Erreur: Le sous-commentaire avec ID " + sousCommentaire.getId() + " n'existe pas.");
        }

        // Mettre à jour le sous-commentaire
        String req = "UPDATE `sous_commentaires` SET `contenu` = ? WHERE `id` = ?";
        ps = cnx.prepareStatement(req);
        ps.setString(1, sousCommentaire.getContenu());
        ps.setInt(2, sousCommentaire.getId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Aucune mise à jour effectuée. Vérifiez l'ID.");
        }

        return rowsAffected;
    }


    @Override
    public int delete(SousCommentaire sousCommentaire) throws SQLException {
        // Vérifier si le sous-commentaire existe avant de le supprimer
        String checkExist = "SELECT id FROM sous_commentaires WHERE id = ?";
        try (PreparedStatement psCheck = cnx.prepareStatement(checkExist)) {
            psCheck.setInt(1, sousCommentaire.getId());
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Erreur: Le sous-commentaire avec ID " + sousCommentaire.getId() + " n'existe pas.");
            }
        }

        // Supprimer le sous-commentaire
        String req = "DELETE FROM sous_commentaires WHERE id = ?";
        try (PreparedStatement psDelete = cnx.prepareStatement(req)) {
            psDelete.setInt(1, sousCommentaire.getId());
            int rowsAffected = psDelete.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Aucune suppression effectuée. Vérifiez l'ID.");
            }
            return rowsAffected;
        }
    }
    // Méthode pour récupérer les sous-commentaires d'un commentaire donné
    public List<SousCommentaire> getSousCommentairesByCommentaireId(int commentaireId) throws SQLException {
        List<SousCommentaire> sousCommentaires = new ArrayList<>();
        String req = "SELECT * FROM sous_commentaires WHERE commentaire_id = ?";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, commentaireId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            SousCommentaire sousCommentaire = new SousCommentaire();
            sousCommentaire.setId(rs.getInt("id"));
            sousCommentaire.setCommentaireId(rs.getInt("commentaire_id"));
            sousCommentaire.setUtilisateurId(rs.getInt("utilisateur_id"));
            sousCommentaire.setContenu(rs.getString("contenu"));
            //sousCommentaire.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());

            sousCommentaires.add(sousCommentaire);
        }

        return sousCommentaires;
    }


    @Override
    public List<SousCommentaire> showAll() throws SQLException {
        List<SousCommentaire> temp = new ArrayList<>();
        String req = "SELECT * FROM `sous_commentaires`";

        st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            SousCommentaire sousCommentaire = new SousCommentaire();
            sousCommentaire.setId(rs.getInt("id"));
            sousCommentaire.setContenu(rs.getString("contenu"));
            sousCommentaire.setCommentaireId(rs.getInt("commentaire_id"));
            sousCommentaire.setUtilisateurId(rs.getInt("utilisateur_id"));
            sousCommentaire.setDateCreation(rs.getString("date_creation"));
            temp.add(sousCommentaire);
        }

        return temp;
    }
/************show comment by id *********/









}
