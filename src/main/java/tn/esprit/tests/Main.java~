package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override

    public void start(Stage primaryStage) throws Exception
    {
        Parent root =FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/Homepage.fxml")));

        primaryStage.setTitle("Home page");
        primaryStage.setScene(new Scene(root,1105,660));
        primaryStage.show();
    }
    public static void main (String []args )
    {
        launch(args);
    }
}
