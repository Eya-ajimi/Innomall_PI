package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {private static final String URL = "jdbc:mysql://localhost:3306/innomall"; // Update with your database name
    private static final String USER = "root";  // Update with your MySQL username
    private static final String PASSWORD = "";  // Update with your MySQL password

    private static DataBase instance;
    private Connection connection;

    // Private constructor to prevent direct instantiation
    private DataBase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connection established.");
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }
    }

    // Public method to provide global access to the instance
    public static DataBase getInstance() {
        if (instance == null) {
            synchronized (DataBase.class) { // Thread safety
                if (instance == null) {
                    instance = new DataBase();
                }
            }
        }
        return instance;
    }

    // Method to get the database connection
    public Connection getConnection() {
        return connection;
    }
}
