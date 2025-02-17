package tn.esprit.services;


import tn.esprit.entites.Poste;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService implements CRUD<Poste> {

    private Connection cnx = DataBase.getInstance().getCnx();
    private Statement st;
    private PreparedStatement ps;

    @Override
    public int insert(Poste poste) throws SQLException {
        // Vérifier que l'utilisateur existe
        String checkUser = "SELECT id FROM utilisateur WHERE id = ?";
        PreparedStatement checkPs = cnx.prepareStatement(checkUser);
        checkPs.setInt(1, poste.getUtilisateurId());
        ResultSet rs = checkPs.executeQuery();

        if (!rs.next()) {
            throw new SQLException("Erreur: L'utilisateur avec ID " + poste.getUtilisateurId() + " n'existe pas.");
        }

        // Insérer le poste
        String req = "INSERT INTO `postes`(`utilisateur_id`, `contenu`) VALUES (?, ?)";
        ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, poste.getUtilisateurId());
        ps.setString(2, poste.getContenu());

        ps.executeUpdate();

        // Récupérer l'ID généré pour le poste
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            poste.setId(generatedKeys.getInt(1));  // Assigner l'ID généré au poste
        }

        return poste.getId();  // Retourner l'ID généré
    }



    @Override
    public int update(Poste poste) throws SQLException {
        // Vérifier si le poste existe avant de le mettre à jour
        String checkPostExists = "SELECT id FROM postes WHERE id = ?";
        ps = cnx.prepareStatement(checkPostExists);
        ps.setInt(1, poste.getId());

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Le poste avec ID " + poste.getId() + " n'existe pas.");
        }

        // Mise à jour du contenu du poste
        String req = "UPDATE postes SET contenu = ? WHERE id = ?";
        ps = cnx.prepareStatement(req);
        ps.setString(1, poste.getContenu());
        ps.setInt(2, poste.getId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Aucune ligne n'a été mise à jour. Vérifiez si le contenu a changé.");
        }

        return rowsAffected;
    }


    public int delete(Poste post) throws SQLException {
        // Vérifier si le poste existe
        String checkPostExists = "SELECT id FROM postes WHERE id = ?";
        ps = cnx.prepareStatement(checkPostExists);
        ps.setInt(1, post.getId());

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Le poste avec ID " + post.getId() + " n'existe pas.");
        }

        // Supprimer d'abord les sous-commentaires associés au poste (via les commentaires)
        String deleteSousCommentaires = "DELETE FROM sous_commentaires WHERE commentaire_id IN (SELECT id FROM commentaires WHERE post_id = ?)";
        ps = cnx.prepareStatement(deleteSousCommentaires);
        ps.setInt(1, post.getId());
        ps.executeUpdate();  // Supprimer tous les sous-commentaires associés au poste

        // Supprimer ensuite les commentaires associés au poste
        String deleteComments = "DELETE FROM commentaires WHERE post_id = ?";
        ps = cnx.prepareStatement(deleteComments);
        ps.setInt(1, post.getId());
        ps.executeUpdate();  // Supprimer tous les commentaires associés au poste

        // Supprimer enfin le poste
        String deletePost = "DELETE FROM postes WHERE id = ?";
        ps = cnx.prepareStatement(deletePost);
        ps.setInt(1, post.getId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Aucune ligne n'a été supprimée.");
        }

        return rowsAffected;
    }







    @Override
    public List<Poste> showAll() throws SQLException {
        List<Poste> posts = new ArrayList<>();
        String req = "SELECT * FROM `postes`";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            System.out.println("Fetching posts from database..."); // Debug
            while (rs.next()) {
                Poste post = new Poste();
                post.setId(rs.getInt("id"));
                post.setContenu(rs.getString("contenu"));
                post.setUtilisateurId(rs.getInt("utilisateur_id"));
                post.setDateCreation(rs.getString("date_creation"));
                posts.add(post);
                System.out.println("Post found: " + post.getContenu()); // Debug
            }

            if (posts.isEmpty()) {
                System.out.println("No posts found in the database."); // Debug
            }
        }

        return posts;
    }
}

