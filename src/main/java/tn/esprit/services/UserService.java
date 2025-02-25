package tn.esprit.services;

import tn.esprit.entites.User;
import tn.esprit.utils.DataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class UserService implements IService<User> {
    private final Connection connection;

    public UserService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, telephone, adresse, date_inscription, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt())); // Hash the password
            pstmt.setString(5, user.getRole());
            pstmt.setString(6, user.getTelephone());
            pstmt.setString(7, user.getAdresse());
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(9, user.getStatut());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Création de l'utilisateur échouée, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, role=?, telephone=?, adresse=?, statut=?, date_inscription=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt())); // Hash the password
            pstmt.setString(5, user.getRole());
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
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotDePasse(rs.getString("mot_de_passe"));
                user.setRole(rs.getString("role"));
                user.setTelephone(rs.getString("telephone"));
                user.setAdresse(rs.getString("adresse"));
                user.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
                user.setStatut(rs.getString("statut"));

                users.add(user);
            }
        }
        return users;
    }

    @Override
    public User getOneById() throws SQLException {
        return null;
    }

    @Override
    public User getOneById(int id) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        User user = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setMotDePasse(rs.getString("mot_de_passe"));
                    user.setRole(rs.getString("role"));
                    user.setTelephone(rs.getString("telephone"));
                    user.setAdresse(rs.getString("adresse"));
                    user.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
                    user.setStatut(rs.getString("statut"));
                }
            }
        }
        return user;
    }

    public User signIn(String email, String password) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        User user = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("mot_de_passe");
                    if (BCrypt.checkpw(password, hashedPassword)) { // Verify the password
                        user = new User();
                        user.setId(rs.getInt("id"));
                        user.setNom(rs.getString("nom"));
                        user.setPrenom(rs.getString("prenom"));
                        user.setEmail(rs.getString("email"));
                        user.setMotDePasse(hashedPassword);
                        user.setRole(rs.getString("role"));
                        user.setTelephone(rs.getString("telephone"));
                        user.setAdresse(rs.getString("adresse"));
                        user.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
                        user.setStatut(rs.getString("statut"));

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
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public int generatePasswordResetToken(String email) throws SQLException {
        Random random = new Random();
        int token= 100000 + random.nextInt(900000);
        String query = "UPDATE utilisateur SET reset_token = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, token);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
        return token;
    }

    public boolean verifyResetCode(String email, String code) throws SQLException {
        String query = "SELECT reset_token FROM utilisateur WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return code.equals(String.valueOf(rs.getInt("reset_token")));
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



}