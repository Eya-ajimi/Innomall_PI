package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entites.Produit;

public class ProduitCardController {
    //@FXML private ImageView produitImageView;
    @FXML private Label nomLabel;
    @FXML private Label prixLabel;
    @FXML private Label stockLabel;
    @FXML private Label Description;

    public void setData(Produit produit) {
        nomLabel.setText(produit.getNom());
        prixLabel.setText("Price: $" + produit.getPrix());
        stockLabel.setText("Stock: " + produit.getStock());
        Description.setText(produit.getDescription());


    }
}