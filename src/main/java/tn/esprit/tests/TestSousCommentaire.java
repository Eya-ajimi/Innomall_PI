package tn.esprit.tests;



import tn.esprit.entites.SousCommentaire;
import tn.esprit.services.SousCommentaireService;

import java.sql.SQLException;

public class TestSousCommentaire {
    public static void main(String[] args) {
        SousCommentaireService sousCommentaireService = new SousCommentaireService();

        // Simulation : L'auteur du post répond à un commentaire
        SousCommentaire sousCommentaire = new SousCommentaire();
        sousCommentaire.setCommentaireId(35); // ID du commentaire
        sousCommentaire.setUtilisateurId(4); // ID de l'auteur du post qui répond
        sousCommentaire.setContenu("Merci pour votre commentaire !");

        try {
            sousCommentaireService.insert(sousCommentaire);
            System.out.println("Sous-commentaire ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
