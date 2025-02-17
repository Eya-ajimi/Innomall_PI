package tn.esprit.utils;

public class TestDataBaseConnection {
    public static void main(String[] args) {
        DataBase db = DataBase.getInstance();
        if (db.getCnx() != null) {
            System.out.println("Connexion à la base de données réussie !");
        } else {
            System.out.println("Échec de la connexion à la base de données.");
        }
    }
}
