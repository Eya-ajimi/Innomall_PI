package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.entites.ATM;
import tn.esprit.entites.Poste;
import tn.esprit.services.ATMService;
import tn.esprit.services.PostService;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.layout.StackPane;

import java.util.List;

import java.io.IOException;
import java.sql.SQLException;

import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;


public class HomePageController {

    @FXML
    private VBox postsContainer;  // This is the container for all posts

    @FXML
    private TextField postTextField;  // TextField where the user writes the post content

    @FXML
    private Button addPostButton;  // Button to trigger adding a post

    private final PostService postService = new PostService();

    @FXML
    public void initialize() throws SQLException {
        loadPosts();  // Load posts when the page initializes
        setupAddPostButton();  // Setup the button to add a post

        atmService = new ATMService(); // Ensure the service is initialized
        loadATMs(); // Load ATMs into the VBox
        sendButton.setOnAction(event -> sendMessage());

        // Set up the send button action
        sendButton.setOnAction(event -> sendMessage());

        // Show chat window when chat icon is clicked
        chatIconButton.setOnAction(event -> {
            chatWindow.setVisible(true);
            chatIconButton.setVisible(false);
        });

        // Hide chat window when close button is clicked
        closeButton.setOnAction(event -> {
            chatWindow.setVisible(false);
            chatIconButton.setVisible(true);
        });


    }
/******** map part****/







/*****aztms part****/

    private ATMService atmService; // Declare the service

    @FXML
    private VBox atmVBox; // Ensure it's correctly linked in FXML
    private void loadATMs() throws SQLException {
        if (atmService == null) {
            System.out.println("ATMService is not initialized!");
            return;
        }

        List<ATM> atms = atmService.showAll(); // Fetch ATMs from DB
        atmVBox.getChildren().clear(); // Clear previous entries

        for (ATM atm : atms) {
            HBox atmEntry = new HBox(10); // HBox for ATM details
            atmEntry.setStyle("-fx-padding: 10px; -fx-border-color: #ddd; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-font-family: 'Arial Black'");

            // Load icon based on status
            ImageView atmIcon = new ImageView(getStatusIcon(atm.getStatus()));
            atmIcon.setFitWidth(30);
            atmIcon.setFitHeight(30);

            // ATM details
            Label atmLabel = new Label("ATM: " + atm.getBankName() + " - Status: " + atm.getStatus());
            atmLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            atmEntry.getChildren().addAll(atmIcon, atmLabel);
            atmVBox.getChildren().add(atmEntry);
        }
    }

    private Image getStatusIcon(String status) {
        String imagePath;

        switch (status.toLowerCase()) {
            case "active":
                imagePath = "/assets/greenatm.png";
                break;
            case "maintenance":
                imagePath = "/assets/yelloatm.png"; // Corrected to yellow icon
                break;
            case "out of service":
                imagePath = "/assets/redatm.png";
                break;
            default:
                imagePath = "/assets/redatm.png"; // Default icon
                break;
        }

        try {
            return new Image(getClass().getResourceAsStream(imagePath));
        } catch (NullPointerException e) {
            System.err.println("Image not found: " + imagePath);
            // Fallback to default image if the specified one isn't found
            return new Image(getClass().getResourceAsStream("/assets/default.png"));
        }
    }

    // Setup the Add Post button's action
    private void setupAddPostButton() {
        addPostButton.setOnAction(event -> {
            String postContent = postTextField.getText().trim();  // Trim to remove extra spaces
            if (postContent.isEmpty()) {
                showAlert("Erreur", "you can't add an empty post !");
            } else {
                Poste newPost = new Poste();
                newPost.setContenu(postContent);
                newPost.setUtilisateurId(17);  // Example user ID, replace with actual user ID if needed

                try {
                    // Insert the post into the database
                    int postId = postService.insert(newPost);

                    // Set the post ID and add it to the UI
                    newPost.setId(postId);
                    addPostToUI(newPost);
                } catch (SQLException e) {
                    e.printStackTrace();  // Handle the exception properly
                }
            }
        });
    }

    // Method to show an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add the new post to the posts container (UI)
    private void addPostToUI(Poste post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostItem.fxml"));
            VBox postBox = loader.load();

            // Get the PostItemController and set post data
            PostItemController controller = loader.getController();
            controller.setPostData(post);

            // Add the post box with its comments to the posts container
            postsContainer.getChildren().add(postBox);

            // Clear the TextField after posting
            postTextField.clear();
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception properly
        }
    }

    // Load posts and their associated comments
    private void loadPosts() {
        try {
            List<Poste> posts = postService.showAll();  // Get all posts

            // Iterate through each post
            for (Poste post : posts) {
                addPostToUI(post);  // Add the post to the UI
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Log the error
        }
    }
    /**************** redirect****/

    @FXML

    private void handleShopsClick() {
        try {
            // Load the Shops.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Shops.fxml"));
            Pane shopsPage = loader.load();

            // Get the current stage
            Stage stage = (Stage) postsContainer.getScene().getWindow(); // Use an existing node to get the stage
            stage.setScene(new Scene(shopsPage));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }









    /*****chatbot*****/

    @FXML
    private StackPane rootPane;

    @FXML
    private VBox chatWindow;

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextField chatInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button chatIconButton;

    @FXML
    private Button closeButton;

    private final MiniChatbot miniChatbot = new MiniChatbot();

    @FXML

    private void sendMessage() {
        String userMessage = chatInput.getText().trim();
        if (!userMessage.isEmpty()) {
            // Add user message to the chat
            addMessageToChat("You: " + userMessage, Color.BLACK, true);

            // Get chatbot response
            String botResponse = miniChatbot.getResponse(userMessage);
            addMessageToChat("Bot: " + botResponse, Color.BLACK, false);

            // Clear the input field
            chatInput.clear();

            // Scroll to the bottom of the chat
            chatScrollPane.setVvalue(1.0);
        }
    }

    private void addMessageToChat(String message, Color color, boolean isUser) {
        Text text = new Text(message);
        text.setFill(color);

        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-padding: 5px; -fx-background-radius: 10px;");

        // Apply different styles for user and bot messages
        if (isUser) {
            textFlow.getStyleClass().add("user-message");
        } else {
            textFlow.getStyleClass().add("bot-message");
        }

        chatBox.getChildren().add(textFlow);
    }

}

