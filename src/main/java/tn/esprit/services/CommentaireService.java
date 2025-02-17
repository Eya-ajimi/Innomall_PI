package tn.esprit.services;



import tn.esprit.entites.Commentaire;
import tn.esprit.entites.SousCommentaire;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService implements CRUD<Commentaire> {

    private Connection cnx = DataBase.getInstance().getCnx();
    private Statement st;
    private PreparedStatement ps;

    public int insert(Commentaire commentaire) throws SQLException {
        String req = "INSERT INTO `commentaires`(`post_id`, `utilisateur_id`, `contenu`) VALUES (?, ?, ?)";
        ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, commentaire.getPosteId());
        ps.setInt(2, commentaire.getUtilisateurId());
        ps.setString(3, commentaire.getContenu());

        int rowsAffected = ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            commentaire.setId(rs.getInt(1)); // Mettre à jour l'ID du commentaire
        }

        return rowsAffected;
    }


    @Override
    public int update(Commentaire commentaire) throws SQLException {
        String req = "UPDATE `commentaires` SET `contenu` = ? WHERE `id` = ?";

        ps = cnx.prepareStatement(req);
        ps.setString(1, commentaire.getContenu());
        ps.setInt(2, commentaire.getId());

        return ps.executeUpdate();
    }

    public int delete(Commentaire commentaire) throws SQLException {
        // Vérifier si le commentaire existe
        String selectQuery = "SELECT COUNT(*) FROM commentaires WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(selectQuery)) {
            stmt.setInt(1, commentaire.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Si le commentaire existe, supprimer d'abord les sous-commentaires associés
                String deleteSousCommentaires = "DELETE FROM sous_commentaires WHERE commentaire_id = ?";
                try (PreparedStatement deleteSousStmt = cnx.prepareStatement(deleteSousCommentaires)) {
                    deleteSousStmt.setInt(1, commentaire.getId());
                    deleteSousStmt.executeUpdate();
                }

                // Supprimer ensuite le commentaire
                String deleteQuery = "DELETE FROM commentaires WHERE id = ?";
                try (PreparedStatement deleteStmt = cnx.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, commentaire.getId());
                    return deleteStmt.executeUpdate();
                }
            } else {
                System.out.println("Le commentaire avec l'ID " + commentaire.getId() + " n'existe pas.");
                return 0;  // Retourne 0 si le commentaire n'existe pas
            }
        }
    }




    @Override
    public List<Commentaire> showAll() throws SQLException {
        List<Commentaire> temp = new ArrayList<>();
        String req = "SELECT * FROM `commentaires`";

        st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Commentaire commentaire = new Commentaire();
            commentaire.setId(rs.getInt("id"));
            commentaire.setContenu(rs.getString("contenu"));
            commentaire.setPostId(rs.getInt("post_id"));
            commentaire.setUtilisateurId(rs.getInt("utilisateur_id"));
            commentaire.setDateCreation(rs.getString("date_creation"));
            temp.add(commentaire);
        }

        return temp;
    }

    public List<Commentaire> getCommentairesByPoste(int postId) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaires WHERE post_id = ?";  // Utilisation de post_id

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commentaire commentaire = new Commentaire(
                        rs.getInt("post_id"), // Changer postId en post_id
                        rs.getInt("utilisateur_id"),
                        rs.getString("contenu")
                );
                commentaire.setId(rs.getInt("id"));
                commentaires.add(commentaire);
            }
        }
        return commentaires;
    }



    // Méthode pour récupérer les sous-commentaires d'un commentaire
    public List<SousCommentaire> getSousCommentaires(int commentaireId) throws SQLException {
        List<SousCommentaire> sousCommentaires = new ArrayList<>();
        String query = "SELECT * FROM sous_commentaires WHERE commentaire_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, commentaireId); // Paramètre pour l'ID du commentaire
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SousCommentaire sousCommentaire = new SousCommentaire();
                sousCommentaire.setId(rs.getInt("id"));
                sousCommentaire.setCommentaireId(rs.getInt("commentaire_id"));
                sousCommentaire.setUtilisateurId(rs.getInt("utilisateur_id"));
                sousCommentaire.setContenu(rs.getString("contenu"));
                //sousCommentaire.setDateCreation(rs.getTimestamp("date_creation"));

                sousCommentaires.add(sousCommentaire);
            }
        }

        return sousCommentaires;
    }



}

