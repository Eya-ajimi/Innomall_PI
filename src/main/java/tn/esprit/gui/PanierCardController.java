package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tn.esprit.entities.Panier;

import java.io.IOException;
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
        prixLabel.setText(String.format("%.2f dt", panier.getPrix()));
    }

    public void setPanierController(PanierController panierController) {
        this.panierController = panierController;
    }

    @FXML
    private void handleAugmenterQuantite() throws SQLException, IOException {
        panierController.handleAugmenterQuantite(panier);
    }

    @FXML
    private void handleDiminuerQuantite() throws SQLException, IOException {
        panierController.handleDiminuerQuantite(panier);
    }

    @FXML
    private void handleSupprimerArticle() throws SQLException, IOException {
        panierController.handleSupprimerArticle(panier);
    }
}