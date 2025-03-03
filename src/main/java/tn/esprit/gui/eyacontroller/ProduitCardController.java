package tn.esprit.gui.eyacontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tn.esprit.entities.Produit;
import tn.esprit.entities.LikeProduit;
import tn.esprit.services.eyaservice.LikeProduitService;
import tn.esprit.services.mariahossservice.panierService;
import tn.esprit.utils.Session;

import java.sql.SQLException;

public class ProduitCardController {
    @FXML private Label nomLabel;
    @FXML private Label prixLabel;
    @FXML private Label stockLabel;
    @FXML private Label Description;
    @FXML private Button likeButton;
    @FXML private Button addToCartButton; // Add to Cart button from FXML

    private Produit currentProduit;

    public void setData(Produit produit) {
        this.currentProduit = produit;
        nomLabel.setText(produit.getNom());
        prixLabel.setText("Price: $" + produit.getPrix());
        stockLabel.setText("Stock: " + produit.getStock());
        Description.setText(produit.getDescription());

        // Using user ID 14 as the current user.
        Session session = Session.getInstance();
        int currentUserId = session.getCurrentUser().getId() ;
        LikeProduitService likeService = new LikeProduitService();

        try {
            // Check if the product is already liked by the current user
            if (likeService.isLiked(currentUserId, produit.getId())) {
                likeButton.setText("Liked");
            } else {
                likeButton.setText("Like");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Toggle like/dislike on left-click
        likeButton.setOnAction(event -> {
            try {
                if (likeButton.getText().equals("Like")) {
                    // Add like
                    LikeProduit like = new LikeProduit(currentUserId, produit.getId());
                    likeService.insert(like);
                    likeButton.setText("Liked");
                } else {
                    // Remove like (dislike)
                    likeService.delete(currentUserId, produit.getId());
                    likeButton.setText("Like");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Add to Cart button action
        addToCartButton.setOnAction(event -> {
            panierService panierService = new panierService();
            try {
                panierService.ajouterAuPanier(currentUserId, produit.getId());
                System.out.println("Produit ajouté au panier avec succès.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de l'ajout du produit au panier.");
            }
        });
    }
}