package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import tn.esprit.entites.Feedback;
import tn.esprit.entites.Utilisateur;
import tn.esprit.services.FeedbackService;

import java.sql.SQLException;

public class ShopCardController {
    @FXML private Label shopName;
    @FXML private Label shopAddress;
    @FXML private Label shopContact;
    @FXML private Button feedbackButton;
    @FXML private HBox ratingContainer;
    @FXML private SVGPath star1, star2, star3, star4, star5;

    private int selectedRating = 0; // Store the selected rating
    private int shopId; // Store the shop ID
    private int utilisateurId; // Store the current user ID (you can get this from your session or login system)

    private FeedbackService feedbackService = new FeedbackService();

    public void setData(Utilisateur shop, int utilisateurId) {
        this.shopId = shop.getId(); // Set the shop ID
        this.utilisateurId = utilisateurId; // Set the current user ID

        shopName.setText(shop.getNom());
        shopAddress.setText(shop.getAdresse());
        shopContact.setText(shop.getTelephone());

        // Initialize rating stars
        initializeRatingStars();
    }

    @FXML
    private void handleFeedbackButtonClick() {
        if (selectedRating == 0) {
            System.out.println("Please select a rating before submitting feedback.");
            return;
        }

        // Debug: Print the utilisateur_id and shop_id
        System.out.println("Submitting feedback for utilisateur_id: " + utilisateurId + ", shop_id: " + shopId);

        // Create a new Feedback object
        Feedback feedback = new Feedback(utilisateurId, shopId, selectedRating);

        try {
            // Insert the feedback into the database
            int feedbackId = feedbackService.insert(feedback);
            System.out.println("Feedback submitted successfully! Feedback ID: " + feedbackId);
        } catch (SQLException e) {
            System.out.println("Error submitting feedback: " + e.getMessage());
            e.printStackTrace(); // Print the full stack trace for debugging
        }
    }

    private void initializeRatingStars() {
        // Add event handlers for rating stars
        star1.setOnMouseClicked(event -> setRating(1));
        star2.setOnMouseClicked(event -> setRating(2));
        star3.setOnMouseClicked(event -> setRating(3));
        star4.setOnMouseClicked(event -> setRating(4));
        star5.setOnMouseClicked(event -> setRating(5));
    }

    private void setRating(int rating) {
        selectedRating = rating;
        updateRatingStars();
    }

    private void updateRatingStars() {
        // Reset all stars to default color
        star1.setFill(javafx.scene.paint.Color.GRAY);
        star2.setFill(javafx.scene.paint.Color.GRAY);
        star3.setFill(javafx.scene.paint.Color.GRAY);
        star4.setFill(javafx.scene.paint.Color.GRAY);
        star5.setFill(javafx.scene.paint.Color.GRAY);

        // Highlight selected stars
        if (selectedRating >= 1) star1.setFill(javafx.scene.paint.Color.GOLD);
        if (selectedRating >= 2) star2.setFill(javafx.scene.paint.Color.GOLD);
        if (selectedRating >= 3) star3.setFill(javafx.scene.paint.Color.GOLD);
        if (selectedRating >= 4) star4.setFill(javafx.scene.paint.Color.GOLD);
        if (selectedRating >= 5) star5.setFill(javafx.scene.paint.Color.GOLD);
    }
}