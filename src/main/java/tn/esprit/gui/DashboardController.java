package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

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
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboardContent() {
        loadView("/DashboardContent.fxml");
    }


    @FXML
    private void showProduits() {
        loadView("/Produit.fxml");
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
    private void showCommandes() {
        loadView("/Commande.fxml");
    }
    @FXML
    private void showProfil() {
        loadView("/Profil.fxml");
    }
}