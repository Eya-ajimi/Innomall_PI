package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private final String USER = "root";
    private final String PWD = "root";
    private final String URL = "jdbc:mysql://localhost:3306/innomall";

    //1st STEP
    public static DataBase instance;

    private Connection cnx;

    //2ND STEP
    private DataBase(){
        try {
            cnx = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("Connection Etablie !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    //3RD STEP
    public static DataBase getInstance(){
        if (instance == null)
            instance = new DataBase();
        return instance;
    }

    public Connection getCnx(){
        return cnx;
    }

}
