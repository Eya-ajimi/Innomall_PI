package tn.esprit.gui.eyacontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entities.Discount;
import tn.esprit.entities.Produit;
import tn.esprit.entities.LikeProduit;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.eyaservice.LikeProduitService;
import tn.esprit.services.mariahossservice.DiscountService;
import tn.esprit.services.mariahossservice.panierService;
import tn.esprit.utils.Session;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class ProduitCardController {
    @FXML private Label nomLabel;
    @FXML private Label prixLabel;
    @FXML private Label stockLabel;
    @FXML private Label Description;
    @FXML private Button likeButton; // Like button from FXML
    @FXML private Label discountLabel;
    @FXML private ImageView imageView;
    @FXML private Button addToCartButton;

    private Produit currentProduit;

    private DiscountService discountService = new DiscountService();

    public void setData(Produit produit) {
        this.currentProduit = produit;
        nomLabel.setText(produit.getNom());
        prixLabel.setText("Price: $" + produit.getPrix());
        stockLabel.setText("Stock: " + produit.getStock());
        Description.setText(produit.getDescription());
        String imagePath = produit.getImage_url();
        Image image;

        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    image = new Image(file.toURI().toString()); // Load from local file system
                    imageView.setImage(image);
                } else {
                    throw new FileNotFoundException("File not found: " + imagePath);
                }
            } else {
                throw new IllegalArgumentException("Image path is null or empty");
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            image = new Image(getClass().getResource("/assets/product.png").toExternalForm()); // Load default image
            imageView.setImage(image);
        }

        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        int currentUserId = currentUser.getId(); // Extract the user ID

        try {
            if (produit.getPromotionId() != 0) { // Check if promotionId is valid
                Discount discount = discountService.getDiscountById(produit.getPromotionId());
                if (discount != null) {
                    double discountPercentage = discount.getDiscountPercentage();
                    double discountedPrice = produit.getPrix() * (1 - discountPercentage / 100);
                    discountLabel.setText(String.format("Discount: %.2f%% (New Price: $%.2f)", discountPercentage, discountedPrice));
                } else {
                    discountLabel.setText("No discount available");
                }
            } else {
                discountLabel.setText("No discount available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            discountLabel.setText("Error fetching discount");
        }


        //int currentUserId = 14;
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