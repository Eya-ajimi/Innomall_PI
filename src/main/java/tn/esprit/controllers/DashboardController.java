package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import javafx.scene.layout.StackPane;

public class DashboardController {

    @FXML
    private AnchorPane mainPane; // Ensure this matches the fx:id in FXML
    @FXML
    private StackPane contentPane;

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane view = loader.load();
            contentPane.getChildren().setAll(view); // Add new content inside the StackPane
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void showProduits() {
        loadView("/Produit.fxml"); // Ensure the path is correct
    }

    @FXML
    private void showPromotion() {
        loadView("/promotion.fxml");
    }

    @FXML
    private void showHoraires() {
        loadView("/horaires.fxml");
    }

    @FXML
    private void showEvenements() {
        loadView("/Evenement.fxml");
    }

    @FXML
    private void showFeedback() {
        loadView("/feedback.fxml");
    }
}