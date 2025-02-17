package tn.esprit.services;

import tn.esprit.entities.Utilisateur;
import tn.esprit.enums.Role;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService {

    private Connection cnx = DataBase.getInstance().getCnx();
    private PreparedStatement ps;

    public int insert(Utilisateur utilisateur) throws SQLException {
        String req = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, adresse, telephone, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

        // Insertion des valeurs dans la requête préparée
        ps.setString(1, utilisateur.getNom());
        ps.setString(2, utilisateur.getPrenom());
        ps.setString(3, utilisateur.getEmail());
        ps.setString(4, utilisateur.getMotDePasse());
        ps.setString(5, utilisateur.getAdresse());
        ps.setString(6, utilisateur.getTelephone());
        ps.setString(7, utilisateur.getRole().name()); // Utilisation correcte de l'énumération Role

        // Exécution de la requête
        ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Erreur: L'ID de l'utilisateur n'a pas été généré.");
        }
    }

    public List<Utilisateur> showAll() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String req = "SELECT * FROM utilisateur";

        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                // Gestion du rôle de manière sécurisée
                Role role = null;
                String roleStr = rs.getString("role");
                if (roleStr != null) {
                    try {
                        role = Role.valueOf(roleStr);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Erreur: Rôle inconnu trouvé en base: " + roleStr);
                    }
                }

                Utilisateur utilisateur = new Utilisateur(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("adresse"),
                        rs.getString("telephone"),
                        role
                );
                utilisateur.setId(rs.getInt("id"));
                utilisateur.setPoints(rs.getInt("points"));
                utilisateur.setNombreDeGain(rs.getInt("nombre_de_gain"));

                utilisateurs.add(utilisateur);
            }
        }
        return utilisateurs;
    }
}
