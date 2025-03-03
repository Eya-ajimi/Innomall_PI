package tn.esprit.gui.mariahosscontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entities.Panier;

import java.io.File;
import java.io.FileNotFoundException;
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
    @FXML
    public ImageView imageView;

    private Panier panier;
    private PanierController panierController;

    public void setPanier(Panier panier) {
        this.panier = panier;
        nomLabel.setText(panier.getNomProduit());
        descriptionLabel.setText(panier.getDescription());
        quantiteLabel.setText(String.valueOf(panier.getQuantite()));
        prixLabel.setText(String.format("%.2f dt", panier.getPrix()));

        String imagePath = panier.getImage_url();
        Image image;

        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    image = new Image(file.toURI().toString()); // Load from local file system
                } else {
                    throw new FileNotFoundException("File not found: " + imagePath);
                }
            } else {
                throw new IllegalArgumentException("Image path is null or empty");
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            image = new Image(getClass().getResource("/assets/product.png").toExternalForm()); // Load default image
        }

        imageView.setImage(image);

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