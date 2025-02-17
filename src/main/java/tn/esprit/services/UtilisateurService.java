package tn.esprit.services;

import tn.esprit.entites.Utilisateur;
import tn.esprit.entites.Role;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService {

    private Connection cnx = DataBase.getInstance().getCnx();
    private PreparedStatement ps;

    public int insert(Utilisateur utilisateur) throws SQLException {
        String req = "INSERT INTO `utilisateur`(`nom`, `prenom`, `email`, `mot_de_passe`, `adresse`, `telephone`, `role`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS); // Récupérer l'ID généré

        // Insertion des valeurs dans la requête préparée
        ps.setString(1, utilisateur.getNom());
        ps.setString(2, utilisateur.getPrenom());
        ps.setString(3, utilisateur.getEmail());
        ps.setString(4, utilisateur.getMotDePasse());
        ps.setString(5, utilisateur.getAdresse());
        ps.setString(6, utilisateur.getTelephone());
        ps.setString(7, utilisateur.getRole().name()); // Correction ici

        // Exécution de la requête
        ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1); // Retourner l'ID de l'utilisateur inséré
        } else {
            throw new SQLException("Erreur: L'ID de l'utilisateur n'a pas été généré.");
        }
    }

    public List<Utilisateur> showAll() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String req = "SELECT * FROM utilisateur";

        // Déclaration de Statement ici
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Utilisateur utilisateur = new Utilisateur(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("adresse"),
                    rs.getString("telephone"),
                    null // Temporairement nul pour éviter erreur, on mettra le rôle après
            );
            utilisateur.setId(rs.getInt("id"));
            utilisateur.setPoints(rs.getInt("points"));
            utilisateur.setNombreDeGain(rs.getInt("nombre_de_gain"));

            // Gestion du rôle avec conversion sécurisée
            String roleStr = rs.getString("role");
            if (roleStr != null) {
                try {
                    utilisateur.setRole(Role.valueOf(roleStr));
                } catch (IllegalArgumentException e) {
                    throw new SQLException("Erreur: Rôle inconnu trouvé en base: " + roleStr);
                }
            }

            utilisateurs.add(utilisateur);
        }

        return utilisateurs;
    }

    public List<Utilisateur> getAllShops() throws SQLException {
        List<Utilisateur> shops = new ArrayList<>();
        String req = "SELECT * FROM utilisateur WHERE role = 'SHOPOWNER'"; // Ensure role matches DB
        System.out.println("Executing query: " + req); // Debugging

        ps = cnx.prepareStatement(req);
        ResultSet rs = ps.executeQuery(req);

        while (rs.next()) {
            String roleStr = rs.getString("role");
            System.out.println("Role found: " + roleStr); // Debugging
            Role role = Role.valueOf(roleStr.toUpperCase()); // Ensure correct enum mapping

            Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getInt("points"),
                    rs.getInt("nombre_de_gain"),
                    rs.getString("adresse"),
                    rs.getString("telephone"),
                    rs.getString("statut"),
                    role
            );
            shops.add(utilisateur);
        }
        System.out.println("Total shops retrieved: " + shops.size()); // Debugging
        return shops;
    }

}
