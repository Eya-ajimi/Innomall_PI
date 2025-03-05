package tn.esprit.gui.eyacontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tn.esprit.entities.Produit;
import tn.esprit.entities.LikeProduit;
import tn.esprit.services.eyaservice.LikeProduitService;

public class ProduitCardController {
    @FXML private Label nomLabel;
    @FXML private Label prixLabel;
    @FXML private Label stockLabel;
    @FXML private Label Description;
    @FXML private Button likeButton; // Like button from FXML

    private Produit currentProduit;

    public void setData(Produit produit) {
        this.currentProduit = produit;
        nomLabel.setText(produit.getNom());
        prixLabel.setText("Price: $" + produit.getPrix());
        stockLabel.setText("Stock: " + produit.getStock());
        Description.setText(produit.getDescription());

        // Using user ID 14 as the current user.
        int currentUserId = 14;
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
    }
}
