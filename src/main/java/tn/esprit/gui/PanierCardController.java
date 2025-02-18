package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tn.esprit.entites.Panier;

import java.sql.SQLException;

public class PanierCardController {

    @FXML
    Label nomLabel;

    @FXML
    Label descriptionLabel;

    @FXML
    Label quantiteLabel;

    @FXML
    public Label prixLabel;

    private Panier panier;
    private PanierController panierController;

    public void setPanier(Panier panier) {
        this.panier = panier;
        nomLabel.setText(panier.getNomProduit());
        descriptionLabel.setText(panier.getDescription());
        quantiteLabel.setText(String.valueOf(panier.getQuantite()));
    }



    public void setPanierController(PanierController panierController) {
        this.panierController = panierController;
    }

    @FXML
    private void handleAugmenterQuantite() throws SQLException {
        panierController.handleAugmenterQuantite(panier);
    }

    @FXML
    private void handleDiminuerQuantite() throws SQLException {
        panierController.handleDiminuerQuantite(panier);
    }

    @FXML
    private void handleSupprimerArticle() throws SQLException {
        panierController.handleSupprimerArticle(panier);
    }
}