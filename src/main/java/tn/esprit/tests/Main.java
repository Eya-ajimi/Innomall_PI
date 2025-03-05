package tn.esprit.tests;

import tn.esprit.utils.DataBase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;

public class Main extends Application {

    public static void main(String[] args) {
        Connection conn = DataBase.getInstance().getCnx();
        if (conn != null) {
            System.out.println("✅ Connexion à la base de données réussie !");
            launch();
        } else {
            System.out.println("❌ Erreur de connexion !");
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("🔍 Tentative de chargement de login.fxml...");

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/login.fxml"));
            Parent root = fxmlLoader.load();

            System.out.println("✅ login.fxml chargé avec succès !");

            Scene scene = new Scene(root, 1400, 600);

            String cssPath = "/css/loginmaria.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                System.out.println("✅ CSS chargé !");
            } else {
                System.out.println("⚠️ Fichier CSS non trouvé !");
            }

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors du chargement de login.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }
}