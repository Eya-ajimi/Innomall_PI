package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private final String USER = "root"; // Nom d'utilisateur MySQL
    private final String PWD = "root"; // Mot de passe MySQL
    private final String URL = "jdbc:mysql://localhost:3306/innomall1"; // URL de la base de données

    // Singleton instance
    private static DataBase instance;

    // Connexion à la base de données
    private Connection cnx;

    // Constructeur privé pour empêcher l'instanciation directe
    private DataBase () {
        try {
            // Charger le pilote JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            cnx = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("Connexion à la base de données réussie !");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Pilote JDBC MySQL non trouvé !");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour obtenir l'instance unique de MyDatabase
    public static DataBase  getInstance() {
        if (instance == null) {
            instance = new DataBase ();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion
    public Connection getCnx() {
        return cnx;
    }

}
