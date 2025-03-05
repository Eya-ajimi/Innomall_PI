package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static DataBase instance;
    private Connection cnx;
     private final String USER = "root"; // Nom d'utilisateur MySQL
    private final String PWD = ""; // Mot de passe MySQL
    private final String URL = "jdbc:mysql://localhost:3306/innomallmaria"; // URL de la base de données

    private DataBase() {
        // Initialize the connection when the instance is created
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
            // Check if the connection is closed or null
            if (cnx == null || cnx.isClosed()) {
                initializeConnection(); // Reinitialize the connection
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to check connection status: " + e.getMessage());
        }
        return cnx;
    }

    private void initializeConnection() {
        try {
            // Explicitly register the driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            cnx = DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
    }

}
