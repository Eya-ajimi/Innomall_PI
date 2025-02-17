package tn.esprit.tests;


import tn.esprit.entites.LikeProduit;
import tn.esprit.services.LikeProduitService;

public class LikeTest {
    public static void main(String[] args) {
        try {
            LikeProduitService likeService = new LikeProduitService();

            // Ajouter un like
            LikeProduit like = new LikeProduit(27, 1); // utilisateurId = 1, produitId = 1
            LikeProduit like1 = new LikeProduit(20, 1);
            int likeId = likeService.insert(like);
            //int likeId1 = likeService.insert(like1);
            System.out.println("Like ajouté avec ID : " + likeId);

            // Supprimer un like
            likeService.delete(20, 1);
            System.out.println("Like supprimé !");

            // Voir les likes d'un produit
            System.out.println("Utilisateurs ayant liké le produit 3 : " + likeService.getLikesByProduit(3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

