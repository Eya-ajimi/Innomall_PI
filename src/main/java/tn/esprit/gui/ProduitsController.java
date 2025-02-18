package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tn.esprit.entites.Produit;
import tn.esprit.services.ProduitService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ProduitsController {
    @FXML private Label shopNameLabel;
    @FXML private VBox produitsContainer;

    private int shopId;
    private ProduitService produitService = new ProduitService();

    // Method to set the shopId and load products
    public void setShopId(int shopId) {
        this.shopId = shopId;
        loadProduits();
    }

    // Method to load products for the shop
    private void loadProduits() {
        try {
            List<Produit> produits = produitService.getProduitsByShopId(shopId);

            for (Produit produit : produits) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProduitCard.fxml"));
                VBox produitCard = loader.load();

                ProduitCardController controller = loader.getController();
                controller.setData(produit);

                produitsContainer.getChildren().add(produitCard);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}