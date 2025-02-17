package tn.esprit.tests;

import tn.esprit.entites.Commentaire;
import tn.esprit.entites.SousCommentaire;
import tn.esprit.services.CommentaireService;
import tn.esprit.services.SousCommentaireService;
import java.sql.SQLException;
import java.util.List;

public class TestDeleteCommentaire {

    public static void main(String[] args) {

        CommentaireService commentaireService = new CommentaireService();
        SousCommentaireService sousCommentaireService = new SousCommentaireService();

        int commentaireId = 33;  // ID du commentaire à supprimer

        try {
            // 1. Récupérer les sous-commentaires liés au commentaire
            List<SousCommentaire> sousCommentaires = sousCommentaireService.getSousCommentairesByCommentaireId(commentaireId);

            // 2. Supprimer les sous-commentaires d'abord
            for (SousCommentaire sousCommentaire : sousCommentaires) {
                int deleteSousCommentaireResult = sousCommentaireService.delete(sousCommentaire);
                if (deleteSousCommentaireResult > 0) {
                    System.out.println("Sous-commentaire ID " + sousCommentaire.getId() + " supprimé avec succès.");
                } else {
                    System.out.println("Erreur lors de la suppression du sous-commentaire ID " + sousCommentaire.getId());
                }
            }

            // 3. Supprimer le commentaire principal après ses sous-commentaires
            Commentaire commentairePrincipal = new Commentaire();
            commentairePrincipal.setId(commentaireId);
            int deleteCommentaireResult = commentaireService.delete(commentairePrincipal);
            if (deleteCommentaireResult > 0) {
                System.out.println("Commentaire ID " + commentaireId + " supprimé avec succès.");
            } else {
                System.out.println("Erreur lors de la suppression du commentaire ID " + commentaireId);
            }

        } catch (SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
