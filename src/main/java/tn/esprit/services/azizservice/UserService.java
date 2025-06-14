package tn.esprit.services.azizservice;

import tn.esprit.entities.Role;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.CRUD;
import tn.esprit.utils.DataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserService implements CRUD<Utilisateur> {
    private final Connection connection;

    public UserService() {
        this.connection = DataBase.getInstance().getCnx();
    }

   /* @Override
    public void create(User user) throws SQLException {
        String req = "INSERT INTO utilisateur(nom, prenom, email, mot_de_passe, adresse, telephone, statut, role, id_categorie) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getEmail());
            pst.setString(4, BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt())); // Hash du mot de passe
            pst.setString(5, user.getAdresse());
            pst.setString(6, user.getTelephone());
            pst.setString(7, user.getStatut());
            pst.setString(8, user.getRole());

            // Gestion de la catégorie pour les Shop Owners
            if (user.getRole().equals("Shop Owner")) {
                pst.setInt(9, getIdCategorieByName(user.getNomCategorie()));
            } else {
                pst.setNull(9, Types.INTEGER); // Pas de catégorie pour les utilisateurs normaux
            }

            pst.executeUpdate(); // Exécuter la requête
        }
    }
*/
   @Override
   public void create(Utilisateur user) throws SQLException {
       String req = "INSERT INTO utilisateur(nom, prenom, email, mot_de_passe, adresse, telephone, statut, role, id_categorie, description) " +
               "VALUES(?,?,?,?,?,?,?,?,?,?)";
       try (PreparedStatement pst = connection.prepareStatement(req)) {
           pst.setString(1, user.getNom());
           pst.setString(2, user.getPrenom());
           pst.setString(3, user.getEmail());
           pst.setString(4, BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt())); // Hash du mot de passe
           pst.setString(5, user.getAdresse());
           pst.setString(6, user.getTelephone());
           pst.setString(7, user.getStatut());
           pst.setString(8, user.getRole().toString());

           // Gestion de la catégorie pour les Shop Owners
           if (user.getRole()== Role.SHOPOWNER) {
               pst.setInt(9, getIdCategorieByName(user.getNomCategorie()));
           } else {
               pst.setNull(9, Types.INTEGER); // Pas de catégorie pour les utilisateurs normaux
           }
           if (user.getRole()== Role.SHOPOWNER) {
               pst.setString(10, user.getDescription()); // Set description for Shop Owner
           } else {
               pst.setNull(10, Types.VARCHAR); // Set description to NULL for other roles
           }


           pst.executeUpdate(); // Exécuter la requête
       }
   }

    @Override
    public int insert(Utilisateur utilisateur) throws SQLException {
        return 0;
    }

    @Override
    public int update(Utilisateur user) throws SQLException {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, role=?, telephone=?, adresse=?, statut=?, date_inscription=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt())); // Hash du mot de passe
            pstmt.setString(5, user.getRole().toString());
            pstmt.setString(6, user.getTelephone());
            pstmt.setString(7, user.getAdresse());
            pstmt.setString(8, user.getStatut());
            pstmt.setTimestamp(9, Timestamp.valueOf(user.getDateInscription()));
            pstmt.setInt(10, user.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Mise à jour de l'utilisateur échouée, aucune ligne affectée.");
            }
        }
        return 0;
    }

    @Override
    public int delete(Utilisateur utilisateur) throws SQLException {
        return 0;
    }

    @Override
    public List<Utilisateur> showAll() throws SQLException {
        return List.of();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Suppression de l'utilisateur échouée, aucune ligne affectée.");
            }
        }
    }

    @Override
    public List<Utilisateur> getAll() throws SQLException {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Utilisateur user = new Utilisateur();
                mapResultSetToUser(rs, user);
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public Utilisateur getOneById() throws SQLException {
        return null;
    }

    @Override
    public Utilisateur getOneById(int id) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        Utilisateur user = null;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new Utilisateur();
                    mapResultSetToUser(rs, user);
                }
            }
        }
        return user;
    }

    // Méthode helper pour mapper ResultSet -> User
    private void mapResultSetToUser(ResultSet rs, Utilisateur user) throws SQLException {
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("mot_de_passe"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setTelephone(rs.getString("telephone"));
        user.setAdresse(rs.getString("adresse"));
        user.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
        user.setStatut(rs.getString("statut"));
        user.setDescription(rs.getString("description"));
        user.setIdCategorie(rs.getInt("id_categorie"));
        user.setProfilePicture(rs.getBytes("profilepicture"));
    }

    public Utilisateur signIn(String email, String password) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        Utilisateur user = null;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("mot_de_passe");
                    if (BCrypt.checkpw(password, hashedPassword)) { // Vérification du mot de passe
                        user = new Utilisateur();
                        mapResultSetToUser(rs, user);
                    }
                }
            }
        }
        return user;
    }

    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int generatePasswordResetToken(String email) throws SQLException {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000); // Génère un token à 6 chiffres
        String query = "UPDATE utilisateur SET reset_token = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, token);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
        return token;
    }

    public boolean verifyResetCode(String email, String code) throws SQLException {
        String query = "SELECT reset_token FROM utilisateur WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return code.equals(String.valueOf(rs.getInt("reset_token")));
                }
            }
        }
        return false;
    }

    public void updatePassword(String email, String newPassword) throws SQLException {
        String query = "UPDATE utilisateur SET mot_de_passe = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt())); // Hash du nouveau mot de passe
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }

    public List<String> getCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String req = "SELECT nom FROM categorie";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    categories.add(rs.getString("nom")); // Add each category to the list
                }
            }
        }
        return categories;
    }

    public int getIdCategorieByName(String nom) throws SQLException {
        String req = "SELECT id_categorie FROM categorie WHERE nom = ?"; // Correction ici
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, nom);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_categorie"); // Correction ici
                }
            }
        }
        throw new SQLException("Catégorie non trouvée");
    }

    // Méthode pour vérifier le mot de passe actuel
    public boolean verifyPassword(int userId, String password) throws SQLException {
        String query = "SELECT mot_de_passe FROM utilisateur WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("mot_de_passe");
                    return BCrypt.checkpw(password, hashedPassword); // Vérifie si le mot de passe correspond
                }
            }
        }
        return false; // Si l'utilisateur n'est pas trouvé ou si le mot de passe ne correspond pas
    }

    // Méthode pour mettre à jour les informations de l'utilisateur
    public void updateUser(Utilisateur user) throws SQLException {
        StringBuilder query = new StringBuilder("UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ?");
        List<Object> parameters = new ArrayList<>();

        // Add common fields
        parameters.add(user.getNom());
        parameters.add(user.getPrenom());
        parameters.add(user.getEmail());
        parameters.add(user.getTelephone());
        parameters.add(user.getAdresse());

        // Debug logs
        System.out.println("Password: " + user.getMotDePasse());
        System.out.println("Profile Picture: " + user.getProfilePicture());

        // Handle password update
        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            query.append(", mot_de_passe = ?");
            parameters.add(BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt()));
            System.out.println("Updating password...");
        }

        // Handle profile picture update
        if (user.getProfilePicture() != null) {
            query.append(", profilepicture = ?");
            parameters.add(user.getProfilePicture());
            System.out.println("Updating profile picture...");
        }

        // Add the WHERE clause
        query.append(" WHERE id = ?");
        parameters.add(user.getId());

        // Debug log for the final query
        System.out.println("Final Query: " + query.toString());

        // Execute the query
        try (PreparedStatement pstmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour de l'utilisateur a échoué.");
            }
        }
    }
    public void updateshopowner(Utilisateur user) throws SQLException {
        String query;
        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            // Update password
            query = "UPDATE utilisateur SET nom = ?, email = ?, mot_de_passe = ?, id_categorie = ?, description = ?, profilepicture = ? WHERE id = ?";
        } else {
            // Do not update password
            query = "UPDATE utilisateur SET nom = ?, email = ?, id_categorie = ?, description = ?, profilepicture = ? WHERE id = ?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
                // With password
                pstmt.setString(1, user.getNom());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt())); // Hashed password
                pstmt.setInt(4, user.getIdCategorie());
                pstmt.setString(5, user.getDescription());
                pstmt.setBytes(6, user.getProfilePicture());
                pstmt.setInt(7, user.getId());
            } else {
                // Without password
                pstmt.setString(1, user.getNom());
                pstmt.setString(2, user.getEmail());
                pstmt.setInt(3, user.getIdCategorie());
                pstmt.setString(4, user.getDescription());
                pstmt.setBytes(5, user.getProfilePicture());
                pstmt.setInt(6, user.getId());
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour de l'utilisateur a échoué, aucune ligne affectée.");
            }
        }
    }

    public String getNomCategorieById(int idCategorie) throws SQLException {
        String nomCategorie = null; // Variable pour stocker le nom de la catégorie
        String query = "SELECT nom FROM categorie WHERE id_categorie = ?"; // Requête SQL

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCategorie); // Définir l'ID de la catégorie dans la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nomCategorie = rs.getString("nom"); // Récupérer le nom de la catégorie
                }
            }
        }

        return nomCategorie; // Retourner le nom de la catégorie
    }


    public List<Utilisateur> getAllShops() throws SQLException {
        List<Utilisateur> shops = new ArrayList<>();
        String req = "SELECT * FROM utilisateur WHERE role = 'SHOPOWNER'"; // Ensure role matches DB
        System.out.println("Executing query: " + req); // Debugging

        try (PreparedStatement pstmt = connection.prepareStatement(req);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Utilisateur shop = new Utilisateur();
                mapResultSetToUser(rs, shop); // Use the existing helper method to map ResultSet to User
                shops.add(shop);
            }
        }

        System.out.println("Total shops retrieved: " + shops.size()); // Debugging
        return shops;
    }

    public List<Utilisateur> getShopsByName(String name) throws SQLException {
        List<Utilisateur> shops = new ArrayList<>();
        String query = "SELECT * FROM utilisateur WHERE role = 'SHOPOWNER' AND nom LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Utilisateur shop = new Utilisateur();
                shop.setId(rs.getInt("id"));
                shop.setNom(rs.getString("nom"));
                shop.setAdresse(rs.getString("adresse"));
                shop.setTelephone(rs.getString("telephone"));
                shops.add(shop);
            }
        }
        return shops;
    }
}
