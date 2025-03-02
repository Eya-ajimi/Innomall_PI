package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.gui.EventController;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event.fxml"));
        Parent root = loader.load();

        // Get the controller instance
        EventController mainController = loader.getController();


        mainController.setIdOrganisateur(1); // Example: Set organizer ID to 2

        // Explicitly call loadEvents() after setting the idOrganisateur
        mainController.loadEvents();

        // Set up the scene and stage
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Shop owner add event");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}