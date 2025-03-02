
package tn.esprit.gui.mariahosscontroller;

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
        loadView("/fxml/DashboardContentmaria.fxml");
    }


    @FXML
    private void showProduits() {
        loadView("/fxml/Produit.fxml");
    }

    @FXML
    private void showPromotion() {
        loadView("/fxml/promotion.fxml");
    }

    @FXML
    private void showHoraires() {
        loadView("/fxml/horaires.fxml");
    }

    @FXML
    private void showEvenements() {
        loadView("/fxml/Evenement.fxml");
    }

    @FXML
    private void showCommandes() {
        loadView("/fxml/Commande.fxml");
    }
    @FXML
    private void showProfil() {
        loadView("/fxml/ProfilShopOwner.fxml");
    }
}