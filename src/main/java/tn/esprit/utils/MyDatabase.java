package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static MyDatabase instance;
    private Connection connection;
    private final String URL = "jdbc:mysql://127.0.0.1:3306/gestevent";
    private final String USER = "root"; // Replace with your MySQL username
    private final String PASSWORD = ""; // Replace with your MySQL password

    private MyDatabase() {
        // Initialize the connection when the instance is created
        initializeConnection();
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Check if the connection is closed or null
            if (connection == null || connection.isClosed()) {
                initializeConnection(); // Reinitialize the connection
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to check connection status: " + e.getMessage());
        }
        return connection;
    }

    private void initializeConnection() {
        try {
            // Explicitly register the driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}