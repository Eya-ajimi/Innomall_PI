package tn.esprit.services.azizservice;

import tn.esprit.entities.Event;
import tn.esprit.entities.Role;
import tn.esprit.entities.Schedule;
import tn.esprit.entities.Utilisateur;
import tn.esprit.entities.enums.Days;
import tn.esprit.services.azizservice.CRUD;
import tn.esprit.utils.DataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;

public class UserService implements CRUD<Utilisateur> {
    private final Connection connection;

    public UserService() {
        this.connection = DataBase.getInstance().getCnx();
    }

    @Override
    public void add(Event event) {

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
                    System.out.println("Fetched user points: " + user.getPoints()); // Debug statement
                }
            }
        }
        return user;
    }

    @Override
    public Event getById(int id) {
        return null;
    }

    // Méthode helper pour mapper ResultSet -> User
    private void mapResultSetToUser(ResultSet rs, Utilisateur user) throws SQLException {
        // Mapper les champs obligatoires
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("mot_de_passe"));
        user.setTelephone(rs.getString("telephone"));
        user.setAdresse(rs.getString("adresse"));
        user.setStatut(rs.getString("statut"));
        user.setIdCategorie(rs.getInt("id_categorie"));
        user.setPoints(rs.getInt("points"));


        // Récupérer le nom de la catégorie à partir de l'ID
        int idCategorie = user.getIdCategorie();
        if (idCategorie > 0) { // Vérifier si l'ID de la catégorie est valide
            String nomCategorie = getNomCategorieById(idCategorie); // Méthode pour récupérer le nom de la catégorie
            user.setNomCategorie(nomCategorie);
        } else {
            user.setNomCategorie("N/A"); // Valeur par défaut si aucune catégorie n'est associée
        }

        // Gestion du rôle avec une valeur par défaut en cas d'erreur
        String roleString = rs.getString("role");
        try {
            user.setRole(Role.valueOf(roleString)); // Convertit la chaîne en enum
        } catch (IllegalArgumentException e) {
            user.setRole(Role.CLIENT); // Rôle par défaut
            System.err.println("Rôle inconnu : " + roleString);
        }

        // Gestion des champs optionnels (peuvent être NULL)
        Timestamp dateInscription = rs.getTimestamp("date_inscription");
        if (dateInscription != null) {
            user.setDateInscription(dateInscription.toLocalDateTime());
        } else {
            user.setDateInscription(null);
        }

        String description = rs.getString("description");
        if (description != null) {
            user.setDescription(description);
        } else {
            user.setDescription("");
        }

        String profilePicture = rs.getString("profilepicture");
        if (profilePicture != null) {
            user.setProfilePicture(profilePicture);
        } else {
            user.setProfilePicture("/assets/7.png"); // Default profile picture path
        }
    }

    public Utilisateur signIn(String email, String password) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        Utilisateur user = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("mot_de_passe");

                    // jBCrypt only supports $2a$, $2b$, etc.  Convert legacy $2y$ hashes
                    if (hashedPassword != null && hashedPassword.startsWith("$2y$")) {
                        hashedPassword = "$2a$" + hashedPassword.substring(4);
                    }

                    // Verify the password
                    if (BCrypt.checkpw(password, hashedPassword)) {
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
                    if (hashedPassword == null || hashedPassword.isEmpty()) {
                        return false;
                    }
                    
                    // Handle different BCrypt versions
                    if (hashedPassword.startsWith("$2y$")) {
                        hashedPassword = "$2a$" + hashedPassword.substring(4);
                    }
                    
                    try {
                        return BCrypt.checkpw(password, hashedPassword);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error verifying password: " + e.getMessage());
                        return false;
                    }
                }
            }
        }
        return false;
    }

    // Nouvelle méthode pour mettre à jour uniquement l'image de profil
    public void updateProfilePicture(int userId, String profilePicturePath) throws SQLException {
        String query = "UPDATE utilisateur SET profilepicture = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, profilePicturePath);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour de l'image de profil a échoué.");
            }
        }
    }

    // Méthode modifiée pour la mise à jour des informations de l'utilisateur
    public void updateUser(Utilisateur user) throws SQLException {
        StringBuilder query = new StringBuilder("UPDATE utilisateur SET nom = ?, email = ?, telephone = ?, adresse = ?");
        List<Object> parameters = new ArrayList<>();
        
        parameters.add(user.getNom());
        parameters.add(user.getEmail());
        parameters.add(user.getTelephone());
        parameters.add(user.getAdresse());

        // Ajouter le mot de passe uniquement s'il est fourni
        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            query.append(", mot_de_passe = ?");
            parameters.add(BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt(12)));
        }

        query.append(" WHERE id = ?");
        parameters.add(user.getId());

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
        StringBuilder query = new StringBuilder("UPDATE utilisateur SET nom = ?, email = ?, id_categorie = ?, description = ?");
        List<Object> parameters = new ArrayList<>();

        // Add basic fields
        parameters.add(user.getNom());
        parameters.add(user.getEmail());
        parameters.add(user.getIdCategorie());
        parameters.add(user.getDescription());

        // Add password only if it's provided
        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            query.append(", mot_de_passe = ?");
            parameters.add(BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt(12)));
        }

        // Add WHERE clause
        query.append(" WHERE id = ?");
        parameters.add(user.getId());

        try (PreparedStatement pstmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour du profil shop owner a échoué.");
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


    public int getCategoryIdByName(String categoryName) throws SQLException {
        String query = "SELECT id_categorie FROM categorie WHERE nom = ?";
        int categoryId = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    categoryId = rs.getInt("id_categorie");
                }
            }
        }
        return categoryId;
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

    // ajouter le profilepic ici
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
                shop.setProfilePicture(rs.getString("profilepicture"));
                shops.add(shop);
            }
        }
        return shops;
    }

    /** horaire*/

    public List<Schedule> getScheduleByShopId(int shopId) throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM schedule WHERE shopId = ? ";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        Days.valueOf(rs.getString("day_of_week")), // Convert string to enum
                        rs.getTime("opening_time"),
                        rs.getTime("closing_time"),
                        rs.getInt("shopId")
                );
                schedules.add(schedule);
            }
        }
        return schedules;
    }


    /** filter*/
    public List<Utilisateur> getShopsByCategory(String category) throws SQLException {
        List<Utilisateur> shops = new ArrayList<>();
        String query = "SELECT u.* FROM utilisateur u JOIN categorie c ON u.id_categorie = c.id_categorie WHERE c.nom = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Utilisateur shop = new Utilisateur();
                    shop.setId(rs.getInt("id"));
                    shop.setNom(rs.getString("nom"));
                    shop.setDescription(rs.getString("description"));
                    shop.setIdCategorie(rs.getInt("id_categorie"));
                    shop.setProfilePicture(rs.getString("profilepicture"));
                    // Add other necessary fields
                    shops.add(shop);
                }
            }
        }

        return shops;
    }

    /**points*/
    public void resetUserPoints(int userId) throws SQLException {
        String sql = "UPDATE utilisateur SET points = 0 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public void updateShopOwneradmin(Utilisateur user) throws SQLException {
        String sql = "UPDATE utilisateur SET "
                + "nom = ?, "
                + "email = ?, "
                + "role = ?, "
                + "id_categorie = ? "
                + "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().toString());
            pstmt.setInt(4, user.getIdCategorie());
            pstmt.setInt(5, user.getId());

            pstmt.executeUpdate();
        }
    }
    public void updateStandardUseradmin(Utilisateur user) throws SQLException {
        String sql = "UPDATE utilisateur SET "
                + "nom = ?, "
                + "prenom = ?, "
                + "email = ?, "
                + "telephone = ?, "
                + "adresse = ?, "
                + "role = ? "
                + "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getTelephone());
            pstmt.setString(5, user.getAdresse());
            pstmt.setString(6, user.getRole().toString());
            pstmt.setInt(7, user.getId());

            pstmt.executeUpdate();
        }
    }

    public Map<String, Integer> getWeeklyRegistrationsByRole(Role role) throws SQLException {
        Map<String, Integer> weeklyRegistrations = new HashMap<>();

        // Requête SQL pour récupérer les inscriptions par semaine pour un rôle donné
        String query = "SELECT YEARWEEK(date_inscription) AS week, COUNT(*) AS count " +
                "FROM utilisateur " +
                "WHERE role = ? " +
                "GROUP BY YEARWEEK(date_inscription) " +
                "ORDER BY week";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, role.toString()); // Convertir le rôle en chaîne pour la requête SQL

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String week = rs.getString("week"); // Récupérer la semaine (format YYYYWW)
                    int count = rs.getInt("count"); // Récupérer le nombre d'inscriptions
                    weeklyRegistrations.put(week, count); // Ajouter à la map
                }
            }
        }

        return weeklyRegistrations;
    }

    public void updateshopownerBasicInfo(Utilisateur user) throws SQLException {
        String query = "UPDATE utilisateur SET nom = ?, email = ?, id_categorie = ?, description = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getIdCategorie());
            pstmt.setString(4, user.getDescription());
            pstmt.setInt(5, user.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour du profil shop owner a échoué.");
            }
        }
    }

    public void updateShopOwnerPassword(int userId, String newPassword) throws SQLException {
        String query = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour du mot de passe a échoué.");
            }
        }
    }

}




