package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private final String USER = "root";
    private final String PWD = "";
    private final String URL = "jdbc:mysql://localhost:3306/InnoMall";
    public static DataBase instance;
    private Connection cnx;

    private DataBase() {
        try {
            this.cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/InnoMall", "root", "");
            System.out.println("Connection Etablie !");
        } catch (SQLException var2) {
            SQLException e = var2;
            System.err.println(e.getMessage());
        }

    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }

        return instance;
    }

    public Connection getCnx() {
        return this.cnx;
    }

}