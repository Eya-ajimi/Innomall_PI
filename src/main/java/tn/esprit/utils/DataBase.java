package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static DataBase instance;
    private Connection cnx;
    private final String USER = "root";
    private final String PWD = "";
    private final String URL = "jdbc:mysql://localhost:3306/webjava?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private DataBase() {
        initializeConnection();
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                initializeConnection();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la connexion : " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
        return cnx;
    }

    private void initializeConnection() {
        try {
            // Vérifier si le driver est disponible
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Tentative de connexion avec timeout de 5 secondes
            DriverManager.setLoginTimeout(5);
            cnx = DriverManager.getConnection(URL, USER, PWD);
            
            System.out.println("Connexion à la base de données établie avec succès!");
        } catch (ClassNotFoundException e) {
            String message = "Driver MySQL introuvable : " + e.getMessage();
            System.err.println(message);
            throw new RuntimeException(message, e);
        } catch (SQLException e) {
            String message = "Impossible de se connecter à la base de données : " + e.getMessage() +
                           "\nVérifiez que :" +
                           "\n1. Le serveur MySQL est démarré" +
                           "\n2. Les identifiants sont corrects" +
                           "\n3. La base de données 'webjava' existe";
            System.err.println(message);
            throw new RuntimeException(message, e);
        }
    }
}
