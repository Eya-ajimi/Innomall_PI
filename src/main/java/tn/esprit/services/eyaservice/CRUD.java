package tn.esprit.services.eyaservice;

import tn.esprit.entities.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {
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
    void create(Utilisateur user) throws SQLException;

    int insert(T t) throws SQLException;
    int update(T t) throws SQLException;
    int delete(T t) throws SQLException;
    List<T> showAll() throws SQLException;

    void delete(int id) throws SQLException;

    List<Utilisateur> getAll() throws SQLException;

    Utilisateur getOneById() throws SQLException;

    Utilisateur getOneById(int id) throws SQLException;
}
