package tn.esprit.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private final String USER = "root";
    private final String PWD = "";
    private final String URL = "jdbc:mysql://localhost:3306/innomall";

    //1st STEP
    public static DataBase instance;

    private Connection cnx;

    //2ND STEP
    private DataBase(){
        try {
            cnx = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("Connection Etablie yeyyy !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    //3RD STEP
    public static DataBase getInstance(){
        if (instance == null) instance = new DataBase();
        return instance;
    }

    public Connection getCnx(){
        return cnx;
    }
    public static void testConnection() {
        Connection testCnx = getInstance().getCnx();
        if (testCnx != null) {
            System.out.println("Database is connected successfully!");
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
