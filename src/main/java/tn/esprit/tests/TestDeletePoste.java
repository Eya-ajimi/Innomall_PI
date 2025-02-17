package tn.esprit.tests;

import tn.esprit.entites.Poste;
import tn.esprit.entites.Commentaire;
import tn.esprit.entites.SousCommentaire;
import tn.esprit.services.PostService;
import tn.esprit.services.CommentaireService;
import tn.esprit.services.SousCommentaireService;

import java.sql.SQLException;
import java.util.List;

public class TestDeletePoste {

    public static void main(String[] args) {

        PostService postService = new PostService();
        CommentaireService commentaireService = new CommentaireService();
        SousCommentaireService sousCommentaireService = new SousCommentaireService();

        // ID du poste à supprimer (post ID 28)
        int postId = 23;

        try {
            // 1. Montrer tous les posts avant suppression
            System.out.println("Tous les posts avant suppression:");
            List<Poste> allPosts = postService.showAll();
            for (Poste post : allPosts) {
                System.out.println(post.getId() + ": " + post.getContenu());
            }

            // 2. Supprimer les sous-commentaires associés au post
            List<Commentaire> commentaires = commentaireService.getCommentairesByPoste(postId);
            for (Commentaire commentaire : commentaires) {
                List<SousCommentaire> sousCommentaires = sousCommentaireService.showAll();  // Récupérer les sous-commentaires pour ce commentaire
                for (SousCommentaire sousCommentaire : sousCommentaires) {
                    if (sousCommentaire.getCommentaireId() == commentaire.getId()) {
                        sousCommentaireService.delete(sousCommentaire);  // Supprimer chaque sous-commentaire
                        System.out.println("Sous-commentaire supprimé.");
                    }
                }

                // Supprimer les commentaires associés au post
                //commentaireService.delete(commentaire);
                System.out.println("Commentaire supprimé.");
            }

            // 3. Supprimer le post avec ID 28
            Poste postToDelete = new Poste();
            postToDelete.setId(postId);
            postService.delete(postToDelete);  // Supprimer le post
            System.out.println("Poste supprimé.");

            // 4. Montrer tous les posts après suppression pour vérifier
            System.out.println("Tous les posts après suppression:");
            allPosts = postService.showAll();
            for (Poste post : allPosts) {
                System.out.println(post.getId() + ": " + post.getContenu());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
