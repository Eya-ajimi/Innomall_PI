package tn.esprit.tests;

import tn.esprit.entities.Utilisateur;
import tn.esprit.enums.Role;
import tn.esprit.services.UtilisateurService;

import java.sql.SQLException;
import java.util.List;

public class TestUtilisateurService {
    public static void main(String[] args) {
        UtilisateurService utilisateurService = new UtilisateurService();

        try {
            // Création et insertion d'administrateur
            Utilisateur admin = new Utilisateur("Zayati", "Sofiene", "sofiene@example.com",
                    "password123", "Rue Tunis", "55321315", Role.ADMIN);
            int adminId = utilisateurService.insert(admin);
            System.out.println("Administrateur ajouté avec ID: " + adminId);

            // Création et insertion d'utilisateur standard
            Utilisateur user = new Utilisateur("John", "Doe", "john.doe@example.com",
                    "userpass", "Avenue Habib", "123456789", Role.CLIENT);
            int userId = utilisateurService.insert(user);
            System.out.println("Utilisateur ajouté avec ID: " + userId);

            // Récupération et affichage de tous les utilisateurs
            List<Utilisateur> utilisateurs = utilisateurService.showAll();
            System.out.println("Liste des utilisateurs:");
            for (Utilisateur u : utilisateurs) {
                System.out.println(u.getId() + " - " + u.getNom() + " " + u.getPrenom() +
                        " (" + u.getRole() + ")");
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
    }
}
