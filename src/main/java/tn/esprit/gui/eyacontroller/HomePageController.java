package tn.esprit.gui.eyacontroller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.entities.ATM;
import tn.esprit.entities.MallData;
import tn.esprit.entities.Poste;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.eyaservice.ATMService;
import tn.esprit.services.eyaservice.PostService;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.services.eyaservice.MallService;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.io.IOException;
import java.sql.SQLException;

import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import tn.esprit.utils.Session;

import org.jxmapviewer.viewer.GeoPosition;
import java.util.function.Consumer;




public class HomePageController {

    @FXML
    private VBox postsContainer;  // This is the container for all posts

    @FXML
    private TextField postTextField;  // TextField where the user writes the post content

    @FXML
    private ImageView userProfileImage;

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

        initializeMap("Mall of Tunis");

        // Set the onMouseClicked event for the user profile image
        userProfileImage.setOnMouseClicked(this::handleUserProfileClick);
    }
    @FXML
    private void handleUserProfileClick(MouseEvent event) {
        try {
            // Load the UserDashboard.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) userProfileImage.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load the user dashboard.");
        }
    }


/******** map part****/




@FXML
private WebView mapWebView;

    // ... existing fields and methods ...


    private void initializeMap(String address) {
        geocodeAddress(address, position -> {
            if (position != null) {
                String html = generateLeafletHtml(position.getLatitude(), position.getLongitude(), address);
                mapWebView.getEngine().loadContent(html);
            } else {
                showAlert("Error", "Could not geocode the mall address.");
            }
        });
    }

    private void geocodeAddress(String address, Consumer<GeoPosition> callback) {
        new Thread(() -> {
            try {
                String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
                String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
                if (jsonArray.isEmpty()) {
                    Platform.runLater(() -> callback.accept(null));
                    return;
                }

                JsonObject firstResult = jsonArray.get(0).getAsJsonObject();
                double lat = firstResult.get("lat").getAsDouble();
                double lon = firstResult.get("lon").getAsDouble();
                GeoPosition position = new GeoPosition(lat, lon);

                Platform.runLater(() -> callback.accept(position));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Geocoding Error", "Failed to retrieve location: " + e.getMessage());
                    callback.accept(null);
                });
            }
        }).start();
    }

    private String generateLeafletHtml(double lat, double lon, String address) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <title>Mall Map</title>"
                + "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet/dist/leaflet.css\" />"
                + "    <script src=\"https://unpkg.com/leaflet/dist/leaflet.js\"></script>"
                + "    <style>"
                + "        #map { height: 600px; width: 500px; }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div id=\"map\"></div>"
                + "    <script>"
                + "        var map = L.map('map').setView([" + lat + ", " + lon + "], 15);"
                + "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {"
                + "            attribution: '© OpenStreetMap contributors'"
                + "        }).addTo(map);"
                + "        L.marker([" + lat + ", " + lon + "]).addTo(map)"
                + "            .bindPopup('" + address + "')"
                + "            .openPopup();"
                + "    </script>"
                + "</body>"
                + "</html>";
    }




    /****/


@FXML private Label mallInfoLabel;

    private final MallService mallService = new MallService();

    @FXML
    private void fetchMallData() {
        try {
            MallData mallData = mallService.getMallData();
            displayMallInfo(mallData);
        } catch (IOException e) {
            mallInfoLabel.setText("Failed to fetch mall data: " + e.getMessage());
        }
    }

    private void displayMallInfo(MallData mallData) {
        if (mallData == null) {
            mallInfoLabel.setText("No mall data available.");
            return;
        }

        StringBuilder info = new StringBuilder();
        info.append("Mall Name: ").append(mallData.getMallName()).append("\n\n");

        // Display stores
        if (mallData.getStores() != null && !mallData.getStores().isEmpty()) {
            info.append("Stores:\n");
            for (MallData.Store store : mallData.getStores()) {
                info.append("- ").append(store.getName())
                        .append(" (Floor ").append(store.getFloor())
                        .append(", ").append(store.getCategory()).append(")\n");
            }
        } else {
            info.append("Stores: No data available.\n");
        }

        // Display events
        if (mallData.getEvents() != null && !mallData.getEvents().isEmpty()) {
            info.append("\nEvents:\n");
            for (MallData.Event event : mallData.getEvents()) {
                info.append("- ").append(event.getName())
                        .append(" (").append(event.getDate())
                        .append(", ").append(event.getLocation()).append(")\n");
            }
        } else {
            info.append("\nEvents: No data available.\n");
        }

        // Display parking
        if (mallData.getParking() != null) {
            info.append("\nParking:\n");
            info.append("- Total Spaces: ").append(mallData.getParking().getTotalSpaces()).append("\n");
            info.append("- Available Spaces: ").append(mallData.getParking().getAvailableSpaces());
        } else {
            info.append("\nParking: No data available.");
        }

        mallInfoLabel.setText(info.toString());
    }



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
                // Get the current user from the session
                Session session = Session.getInstance();
                Utilisateur currentUser = session.getCurrentUser();

                if (currentUser == null) {
                    showAlert("Erreur", "No user is currently logged in.");
                    return;
                }

                Poste newPost = new Poste();
                newPost.setContenu(postContent);
                newPost.setUtilisateurId(currentUser.getId());  // Use the current user's ID

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
    private void loadPosts() {
        try {
            List<Poste> posts = postService.showAll();  // Get all posts (already sorted by date_creation)

            // Iterate through each post and add it to the UI
            for (Poste post : posts) {
                addPostToUI(post);  // Add the post to the UI
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Log the error
        }
    }

    private void addPostToUI(Poste post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostItem.fxml"));
            VBox postBox = loader.load();

            // Get the PostItemController and set post data
            PostItemController controller = loader.getController();
            controller.setPostData(post);

            // Add the post box with its comments to the top of the posts container
            postsContainer.getChildren().add(0, postBox);  // Insert at the top

            // Clear the TextField after posting
            postTextField.clear();
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception properly
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


    /*event navigation**/


    @FXML

    private void handleEventsClick() {
        try {
            // Load the Shops.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client_event_view.fxml"));
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

