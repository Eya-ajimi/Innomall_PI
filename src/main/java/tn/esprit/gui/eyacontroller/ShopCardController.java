package tn.esprit.gui.eyacontroller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import tn.esprit.entities.Feedback;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.azizservice.UserService;
import tn.esprit.services.eyaservice.FeedbackService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.sql.SQLException;

public class ShopCardController {
    @FXML private Label shopName;
    @FXML private Label shopCategory; // Changed from shopAddress to shopCategory
    @FXML private Label description;
    @FXML private Button feedbackButton;
    @FXML private Button deleteFeedbackButton;
    @FXML private HBox ratingContainer;
    @FXML private SVGPath star1, star2, star3, star4, star5;
    @FXML private Pane shopCardPane;
    @FXML public Button viewProductButton;

    private int selectedRating = 0; // Store the selected rating
    private int shopId; // Store the shop ID
    private int utilisateurId; // Store the current user ID
    private Feedback existingFeedback; // Store existing feedback (if any)
    private BooleanProperty hasFeedback = new SimpleBooleanProperty(false); // Track if feedback exists
    private FeedbackService feedbackService = new FeedbackService();
    private UserService userService = new UserService(); // Add UserService

    @FXML
    public void initialize() {
        // Bind the visibility of the delete button to the hasFeedback property
        deleteFeedbackButton.visibleProperty().bind(hasFeedback);
    }

    public void setData(Utilisateur shop) {
        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        this.shopId = shop.getId(); // Set the shop ID
        this.utilisateurId = currentUser.getId(); // Set the current user ID from the session

        shopName.setText(shop.getNom());
        description.setText(shop.getDescription());

        // Fetch and display the category name
        try {
            String categoryName = userService.getNomCategorieById(shop.getIdCategorie());
            shopCategory.setText(categoryName); // Set the category name
        } catch (SQLException e) {
            e.printStackTrace();
            shopCategory.setText("Category not found"); // Fallback in case of error
        }

        // Check if the user has already submitted feedback for this shop
        try {
            existingFeedback = feedbackService.getFeedbackByUserAndShop(utilisateurId, shopId);
            if (existingFeedback != null) {
                // If feedback exists, set the selected rating and update the button text
                selectedRating = existingFeedback.getRating();
                feedbackButton.setText("Modify Feedback");
                hasFeedback.set(true); // Set hasFeedback to true
            } else {
                hasFeedback.set(false); // Set hasFeedback to false
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Initialize rating stars
        initializeRatingStars();
        updateRatingStars(); // Highlight stars based on existing feedback
    }

    @FXML
    public void navigateToProductsView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProduitsView.fxml"));
        Parent root = loader.load();

        ProduitsController controller = loader.getController();
        controller.setShopId(shopId);

        Stage stage = (Stage) viewProductButton.getScene().getWindow();  // Use button's scene
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleFeedbackButtonClick() {
        // Check if no rating is selected
        if (selectedRating == 0) {
            // Show an alert to the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Rating Required");
            alert.setHeaderText("No Rating Selected");
            alert.setContentText("Please select a rating before submitting feedback.");
            alert.showAndWait(); // Show the alert and wait for user response
            return; // Exit the method without proceeding
        }

        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        try {
            if (existingFeedback == null) {
                // Insert new feedback
                Feedback feedback = new Feedback(currentUser.getId(), shopId, selectedRating);
                int feedbackId = feedbackService.insert(feedback);
                System.out.println("Feedback submitted successfully! Feedback ID: " + feedbackId);

                // Update the button text and store the new feedback
                feedbackButton.setText("Modify Feedback");
                existingFeedback = feedback;
                hasFeedback.set(true); // Set hasFeedback to true
            } else {
                // Update existing feedback
                existingFeedback.setRating(selectedRating);
                feedbackService.update(existingFeedback);
                System.out.println("Feedback updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error submitting feedback: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteFeedbackButtonClick() {
        if (existingFeedback != null) {
            try {
                feedbackService.delete(existingFeedback.getId());
                System.out.println("Feedback deleted successfully!");

                // Reset the UI
                existingFeedback = null;
                selectedRating = 0;
                feedbackButton.setText("Add Feedback");
                hasFeedback.set(false); // Set hasFeedback to false
                updateRatingStars();
            } catch (SQLException e) {
                System.out.println("Error deleting feedback: " + e.getMessage());
            }
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