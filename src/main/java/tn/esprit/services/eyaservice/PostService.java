package tn.esprit.services.eyaservice;


import tn.esprit.entities.Poste;
import tn.esprit.entities.Utilisateur;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService implements CRUD<Poste> {

    private Connection cnx = DataBase.getInstance().getCnx();
    private Statement st;
    private PreparedStatement ps;

    @Override
    public void create(Utilisateur user) throws SQLException {

    }

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

        // Insérer le poste avec la date de création
        String req = "INSERT INTO `postes`(`utilisateur_id`, `contenu`, `date_creation`) VALUES (?, ?, ?)";
        ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, poste.getUtilisateurId());
        ps.setString(2, poste.getContenu());
        ps.setString(3, java.time.LocalDateTime.now().toString());  // Ajouter la date de création actuelle

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
        // Check if the post exists and the user is the creator
        String checkPost = "SELECT id FROM postes WHERE id = ? AND utilisateur_id = ?";
        ps = cnx.prepareStatement(checkPost);
        ps.setInt(1, poste.getId());
        ps.setInt(2, poste.getUtilisateurId());

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Vous n'êtes pas autorisé à modifier ce post.");
        }

        // Proceed with the update
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

    @Override
    public int delete(Poste post) throws SQLException {
        // Check if the post exists and the user is the creator
        String checkPost = "SELECT id FROM postes WHERE id = ? AND utilisateur_id = ?";
        ps = cnx.prepareStatement(checkPost);
        ps.setInt(1, post.getId());
        ps.setInt(2, post.getUtilisateurId());

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Vous n'êtes pas autorisé à supprimer ce post.");
        }

        // Delete associated sous-commentaires
        String deleteSousCommentaires = "DELETE FROM sous_commentaires WHERE commentaire_id IN (SELECT id FROM commentaires WHERE post_id = ?)";
        ps = cnx.prepareStatement(deleteSousCommentaires);
        ps.setInt(1, post.getId());
        ps.executeUpdate();

        // Delete associated commentaires
        String deleteComments = "DELETE FROM commentaires WHERE post_id = ?";
        ps = cnx.prepareStatement(deleteComments);
        ps.setInt(1, post.getId());
        ps.executeUpdate();

        // Delete the post
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
        String req = "SELECT * FROM `postes` ORDER BY `date_creation` ASC ";  // Trier par date de création décroissante

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            System.out.println("Fetching posts from database..."); // Debug
            while (rs.next()) {
                Poste post = new Poste();
                post.setId(rs.getInt("id"));
                post.setContenu(rs.getString("contenu"));
                post.setUtilisateurId(rs.getInt("utilisateur_id"));
                post.setDateCreation(rs.getString("date_creation"));  // Récupérer la date de création
                posts.add(post);
                System.out.println("Post found: " + post.getContenu()); // Debug
            }

            if (posts.isEmpty()) {
                System.out.println("No posts found in the database."); // Debug
            }
        }

        return posts;
    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public List<Utilisateur> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Utilisateur getOneById() throws SQLException {
        return null;
    }

    @Override
    public Utilisateur getOneById(int id) throws SQLException {
        return null;
    }
}
