package tn.esprit.tests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class MainFx extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/logo.png")));

        primaryStage.setTitle("InnoMall");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
