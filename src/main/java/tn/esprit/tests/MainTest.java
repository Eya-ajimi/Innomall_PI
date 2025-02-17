package tn.esprit.tests;



import tn.esprit.entites.*;
import tn.esprit.services.UtilisateurService;
import tn.esprit.services.PostService;
import tn.esprit.services.CommentaireService;
import tn.esprit.services.SousCommentaireService;
import tn.esprit.utils.DataBase;

import java.sql.SQLException;
import java.util.List;

/*public class MainTest {

    public static void main(String[] args) {
        DataBase m1 = DataBase.getInstance();

        // Création des objets utilisateur
        Utilisateur utilisateur1 = new Utilisateur("eya", "eya", "eya1.eya@example.com", "password123", "123 Rue Exemple", "0123456789", "client");
        Utilisateur utilisateur2 = new Utilisateur("Martin", "martin", "martin1.martin1@example.com", "password321", "456 Rue Exemple", "9876543210", "client");

        // Création du service pour ajouter des utilisateurs
        UtilisateurService utilisateurService = new UtilisateurService();

        // Création des objets poste
        Poste poste1 = new Poste(utilisateur1.getId(), "hello everybodyy");
        Poste poste2 = new Poste(utilisateur2.getId(), "coucouu les babieees ");

        // Création du service pour gérer les posts
        PostService posteService = new PostService();

        // Création des objets commentaire
        Commentaire commentaire1 = new Commentaire(poste1.getId(), utilisateur2.getId(), "Commentaire sur le poste 1");
        Commentaire commentaire2 = new Commentaire(poste2.getId(), utilisateur1.getId(), "Commentaire sur le poste 2");

        // Création du service pour gérer les commentaires
        CommentaireService commentaireService = new CommentaireService();

        // Création des objets sous-commentaire
        SousCommentaire sousCommentaire1 = new SousCommentaire(commentaire1.getId(), utilisateur1.getId(), "Sous-commentaire sur le commentaire 1");
        SousCommentaire sousCommentaire2 = new SousCommentaire(commentaire2.getId(), utilisateur2.getId(), "Sous-commentaire sur le commentaire 2");

        // Création du service pour gérer les sous-commentaires
        SousCommentaireService sousCommentaireService = new SousCommentaireService();

        try {
            // Test de l'insertion d'utilisateurs
            utilisateurService.insert(utilisateur1);
            utilisateurService.insert(utilisateur2);

            // Test de l'insertion des posts
            posteService.insert(poste1);
            posteService.insert(poste2);

            // Test de l'insertion des commentaires
            commentaireService.insert(commentaire1);
            commentaireService.insert(commentaire2);

            // Test de l'insertion des sous-commentaires
            sousCommentaireService.insert(sousCommentaire1);
            sousCommentaireService.insert(sousCommentaire2);

            // Afficher tous les utilisateurs, posts, commentaires, et sous-commentaires
            System.out.println("Utilisateurs: " + utilisateurService.showAll());
            System.out.println("Posts: " + posteService.showAll());
            System.out.println("Commentaires: " + commentaireService.showAll());
            System.out.println("Sous-commentaires: " + sousCommentaireService.showAll());

        } catch (SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}*/







/*public class MainTest {
    public static void main(String[] args) {
        UtilisateurService utilisateurService = new UtilisateurService();
        PostService posteService = new PostService();

        try {
            // Créer et insérer un utilisateur
            Utilisateur utilisateur = new Utilisateur("eyaaaa", "eyaa", "jimy2y.dupont@example.com", "password123", "123 Rue Exemple", "0123456789", "client");
            int userId = utilisateurService.insert(utilisateur);
            System.out.println("Utilisateur inséré avec ID : " + userId);

            Poste poste = new Poste(userId, "Ceci est un nouveau poste22.");
            posteService.insert(poste);

// Mise à jour du poste
            poste.setContenu("Le contenu a été mis à jour yesssss.");
            int rowsUpdated = posteService.update(poste);
            System.out.println("Nombre de lignes affectées par la mise à jour : " + rowsUpdated);
            System.out.println("Posts: " + posteService.showAll());
// Suppression du poste
            int rowsDeleted = posteService.delete(poste);
            System.out.println("Nombre de lignes affectées par la suppression : " + rowsDeleted);
            System.out.println("Posts: " + posteService.showAll());

        } catch (SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}*/



/********** partie commentaire */

/*public class MainTest {
    public static void main(String[] args) {
        UtilisateurService utilisateurService = new UtilisateurService();
        PostService posteService = new PostService();
        CommentaireService commentaireService = new CommentaireService();

        try {
            // Créer et insérer un utilisateur
          /*  Utilisateur utilisateur = new Utilisateur("KFC", "KFC", "shop.shop@example.com", "password123", "223 Rue Exemple", "0123456789", "shopowner");
            int userId = utilisateurService.insert(utilisateur);
            System.out.println("Utilisateur inséré avec ID : " + userId);*/

            // Créer et insérer un poste
            //Poste poste = new Poste(27, "Ceci est un nouveau poste pour les tests de commentaire by eya14 .");
           /* int postId = 17;

            // Créer un commentaire
            Commentaire commentaire = new Commentaire(postId, 27, "Ceci est un commentaire sur le poste par jimmy14.");
            commentaireService.insert(commentaire);
            System.out.println("Commentaire inséré avec ID : " + commentaire.getId());

            // Afficher tous les commentaires
            List<Commentaire> commentaires = commentaireService.showAll();
            System.out.println("Commentaires : " + commentaires);

            // Mise à jour du commentaire
            commentaire.setContenu("Le contenu du commentaire a été mis à jour  le 14.");
            commentaireService.update(commentaire);
            System.out.println("Commentaire mis à jour : " + commentaire.getContenu());
            System.out.println("Commentaires: " + commentaireService.showAll());



            //int rowsDeleted = posteService.delete(postId);

            // Suppression du commentaire

                 /* commentaireService.delete(commentaire);
            System.out.println("Commentaire supprimé.");
            System.out.println("Commentaires: " + commentaireService.showAll());
        } catch (SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}*/


/*public class MainTest {
    public static void main(String[] args) {
        try {
            PostService postService = new PostService();
            Poste post = new Poste();
            post.setId(17); // ID du poste à supprimer

            int result = postService.delete(post);
            System.out.println("Nombre de lignes supprimées : " + result);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

    }}*/

/// sous commentaires

public class MainTest {
    public static void main(String[] args) {
        try {
            SousCommentaireService sousCommentaireService = new SousCommentaireService();
            UtilisateurService utilisateurService = new UtilisateurService();
            Utilisateur utilisateur1 = new Utilisateur("ZARA", "ZARA", "ZARA1.ZARA@example.com", "password123", "223 Rue Exemple", "0123456789", Role.SHOPOWNER);
            int userId = utilisateurService.insert(utilisateur1);
            System.out.println("Utilisateur inséré avec ID : " + userId);

            // 📌 1. Insérer un sous-commentaire
            SousCommentaire newSousCommentaire = new SousCommentaire();
            newSousCommentaire.setContenu("Ceci est un sous-commentaire test2.");
            newSousCommentaire.setCommentaireId(35); // Remplace par un ID valide d'un commentaire existant
            newSousCommentaire.setUtilisateurId(27); // Remplace par un ID valide d'un utilisateur existant

            int insertedRows = sousCommentaireService.insert(newSousCommentaire);
            System.out.println("✅ Sous-commentaire inséré : " + insertedRows + " ligne(s) ajoutée(s)");

            // 📌 2. Modifier un sous-commentaire
            newSousCommentaire.setContenu("Ceci est un sous-commentaire mis à jour.");
            int updatedRows = sousCommentaireService.update(newSousCommentaire);
            System.out.println("✅ Sous-commentaire mis à jour : " + updatedRows + " ligne(s) modifiée(s)");

            // 📌 3. Afficher tous les sous-commentaires
            List<SousCommentaire> sousCommentaires = sousCommentaireService.showAll();
            System.out.println("📌 Liste des sous-commentaires :");
            for (SousCommentaire sc : sousCommentaires) {
                System.out.println(sc);
            }

            // 📌 4. Supprimer un sous-commentaire
           /* int deletedRows = sousCommentaireService.delete(newSousCommentaire);
            System.out.println("❌ Sous-commentaire supprimé : " + deletedRows + " ligne(s) supprimée(s)");*/

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }
    }
}
