package tn.esprit.gui.eyacontroller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tn.esprit.entities.Commentaire;
import tn.esprit.entities.Poste;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.eyaservice.CommentaireService;
import tn.esprit.services.eyaservice.PostService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PostItemController {

    @FXML
    private VBox postCard;  // VBox for the post card layout

    @FXML
    private VBox commentsContainer;  // Container for comments

    @FXML
    private Label postUsername;  // Username label

    @FXML
    private Label postDate;  // Date label

    @FXML
    private TextField commentInputField;  // Input field for comments

    @FXML
    private Button addCommentButton;  // Button to add a comment

    @FXML
    private Label postContent;  // Post content label

    @FXML
    private Button editDeleteButton;  // Button to trigger context menu

    private Poste currentPost;  // Store the current post
    private final PostService postService = new PostService();  // Post service for CRUD operations
    private final CommentaireService commentaireService = new CommentaireService();  // Comment service


    public void setPostData(Poste post) {
        this.currentPost = post;  // Store the current post
        postUsername.setText("Utilisateur " + post.getUtilisateurId());
        postDate.setText(post.getDateCreation());
        postContent.setText(post.getContenu());
        setupEditDeleteMenu(post);  // Set up the edit/delete menu
        loadComments(post.getId());  // Load comments for this post
    }




    @FXML
    private void handleAddComment() {
        String content = commentInputField.getText().trim();
        if (content.isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide !", Alert.AlertType.ERROR);
            return;
        }

        // Check for bad words
        if (BadWordChecker.containsBadWords(content)) {
            showAlert("Erreur", "Le commentaire contient des mots inappropriés.", Alert.AlertType.ERROR);
            return;
        }

        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.", Alert.AlertType.ERROR);
            return;
        }

        Commentaire newComment = new Commentaire();
        newComment.setPostId(currentPost.getId());
        newComment.setUtilisateurId(currentUser.getId()); // Use the current user's ID
        newComment.setContenu(content);

        try {
            if (commentaireService.insert(newComment) > 0) {
                commentInputField.clear();
                loadComments(currentPost.getId());
            } else {
                showAlert("Erreur", "Échec de l'ajout du commentaire.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème lors de l'ajout du commentaire.", Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadComments(int postId) {
        try {
            // Fetch comments associated with the post
            List<Commentaire> commentaires = commentaireService.getCommentairesByPoste(postId);

            // Clear the comments container before adding new comments
            commentsContainer.getChildren().clear();

            // Check if there are comments to show
            if (commentaires == null || commentaires.isEmpty()) {
                // Show a message if no comments exist for this post
                Label noCommentsLabel = new Label("No comments yet.");
                commentsContainer.getChildren().add(noCommentsLabel);
            } else {
                // Loop through the comments and add them to the comments container
                for (Commentaire commentaire : commentaires) {
                    FXMLLoader commentLoader = new FXMLLoader(getClass().getResource("/fxml/CommentItem.fxml"));
                    HBox commentBox = commentLoader.load();

                    // Get the CommentItemController to set the comment data
                    CommentItemController commentController = commentLoader.getController();
                    commentController.setCommentData(commentaire);

                    // Add the comment to the comments container
                    commentBox.getStyleClass().add("comment-box");  // Apply CSS class for styling
                    commentsContainer.getChildren().add(commentBox);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();  // Log the error
            // Optionally, display a user-friendly message
            Label errorLabel = new Label("Error loading comments.");
            commentsContainer.getChildren().add(errorLabel);
        }
    }


    private void setupEditDeleteMenu(Poste post) {
        // Create the context menu
        ContextMenu contextMenu = new ContextMenu();

        // Menu item for editing the post
        MenuItem editMenuItem = new MenuItem("Edit Post");
        editMenuItem.setOnAction(e -> editPost(post));

        // Menu item for deleting the post
        MenuItem deleteMenuItem = new MenuItem("Delete Post");
        deleteMenuItem.setOnAction(e -> deletePost(post));

        // Add the menu items to the context menu
        contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);

        // Set the button action to show the menu on click
        editDeleteButton.setOnMouseClicked(event -> {
            contextMenu.show(editDeleteButton, event.getScreenX(), event.getScreenY());
        });
    }


    private void editPost(Poste post) {
        // Create a custom dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Modifier le Post");

        // Add OK and Cancel buttons
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Create a styled text field
        TextField textField = new TextField(post.getContenu());
        textField.setPrefWidth(300);
        textField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-radius: 5px;");

        // Layout
        VBox vbox = new VBox(10, textField);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        // Apply styles to the dialog
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-radius: 10px;");

        // Add animation effects
        dialog.setOnShowing(event -> {
            dialog.getDialogPane().setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialog.getDialogPane());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        // Handle the user's response
        dialog.setResultConverter(button -> {
            if (button == okButton) {
                return textField.getText();
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newContent -> {
            if (!newContent.trim().isEmpty()) {
                post.setContenu(newContent);  // Update the post content
                postContent.setText(newContent);  // Update the UI
                updatePostInDatabase(post);  // Update the database
            }
        });
    }


    private void updatePostInDatabase(Poste post) {
        try {
            postService.update(post);  // Call the update method in the service
            System.out.println("Post mis à jour avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du post : " + e.getMessage());
        }
    }


    private void deletePost(Poste post) {
        System.out.println("Deleting post: " + post.getId());
        try {
            int rowsAffected = postService.delete(post);  // Delete the post using the service
            if (rowsAffected > 0) {
                postCard.setVisible(false);  // Hide the post card after deletion
                System.out.println("Post deleted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();  // Handle error
        }
    }
}