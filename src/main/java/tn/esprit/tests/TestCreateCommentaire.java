package tn.esprit.tests;


import tn.esprit.entites.Commentaire;
import tn.esprit.services.CommentaireService;
import tn.esprit.utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TestCreateCommentaire {

        public static void main(String[] args) {

                CommentaireService commentaireService = new CommentaireService();

                // 1. Test: Create a new Commentaire
                Commentaire newComment = new Commentaire();
                newComment.setPostId(5); // Associer au post ID 23
                newComment.setUtilisateurId(15); // Associer à l'utilisateur ID 29
                newComment.setContenu("Ceci est un commentaire test pour le post 5 par 15");

                try {
                    // Insert the comment
                    int rowsAffected = commentaireService.insert(newComment);
                    if (rowsAffected > 0) {
                        System.out.println("Commentaire créé avec succès, ID = " + newComment.getId());
                    } else {
                        System.out.println("Erreur lors de la création du commentaire.");
                    }

                    // 2. Test: Show all comments
                    System.out.println("Tous les commentaires:");
                    List<Commentaire> allComments = commentaireService.showAll();
                    for (Commentaire c : allComments) {
                        System.out.println("ID: " + c.getId() + ", Contenu: " + c.getContenu());
                    }

                    // 3. Test: Update the Commentaire
                    newComment.setContenu("Le contenu du commentaire a été mis à jour.");
                    int updateResult = commentaireService.update(newComment);
                    if (updateResult > 0) {
                        System.out.println("Commentaire mis à jour avec succès.");
                    } else {
                        System.out.println("Erreur lors de la mise à jour du commentaire.");
                    }

                    // 4. Test: Show comments again after update
                    System.out.println("Commentaires après mise à jour:");
                    List<Commentaire> updatedComments = commentaireService.showAll();
                    for (Commentaire c : updatedComments) {
                        System.out.println("ID: " + c.getId() + ", Contenu: " + c.getContenu());
                    }

                     //5. Test: Delete the Commentaire
                    /*int deleteResult = commentaireService.delete(newComment);
                    if (deleteResult > 0) {
                        System.out.println("Commentaire supprimé avec succès.");
                    } else {
                        System.out.println("Erreur lors de la suppression du commentaire.");
                    }*/

                    /* 6. Test: Show comments after deletion
                    System.out.println("Commentaires après suppression:");
                    List<Commentaire> finalComments = commentaireService.showAll();
                    for (Commentaire c : finalComments) {
                        System.out.println("ID: " + c.getId() + ", Contenu: " + c.getContenu());
                    }*/

                } catch (SQLException e) {
                    System.out.println("Erreur: " + e.getMessage());
                }
        }
    }


//supprimer commenatiare qui a un sous commentaire



