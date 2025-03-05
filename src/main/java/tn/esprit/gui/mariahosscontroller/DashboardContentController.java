package tn.esprit.gui.mariahosscontroller;

import com.google.api.client.util.DateTime;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.services.mariahossservice.*;
import tn.esprit.utils.Session;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class DashboardContentController implements Initializable {
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private FlowPane topProductsPane;

    @FXML
    private AnchorPane dashboardMainPane;

    @FXML
    private VBox emptyStatePane;

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label totalLikesLabel;

    @FXML
    private Label avgRatingLabel;

    @FXML
    private PieChart ratingChart;

    @FXML
    private VBox ratingStatsContainer;
    @FXML
    private Button refreshButton;
    @FXML
    private Button summaryButton;

    private ProductService productService;
    private LikedProductService likedProductService;
    private FeedbackService feedbackService;

    private Session session = Session.getInstance(); // set from login or session

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    // Define chart colors array for consistent styling
    private static final String[] CHART_COLORS = {
            "#3498db", // Blue
            "#2ecc71", // Green
            "#f1c40f", // Yellow
            "#e74c3c", // Red
            "#9b59b6"  // Purple
    };

    public void initialize(URL location, ResourceBundle resources) {
        productService = new ProductService();
        likedProductService = new LikedProductService();
        feedbackService = new FeedbackService();
        summaryButton.setOnAction(event -> showpopresume());
        summaryButton.setOnMouseEntered(event -> summaryButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        summaryButton.setOnMouseExited(event -> summaryButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff791f; -fx-background-radius: 4;"));
        summaryButton.setOnMousePressed(event -> summaryButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4;"));
        summaryButton.setOnMouseReleased(event -> summaryButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        //+++++++++++++++++++++
        refreshButton.setOnMouseEntered(event -> refreshButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));
        refreshButton.setOnMouseExited(event -> refreshButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff791f; -fx-background-radius: 4;"));
        refreshButton.setOnMousePressed(event -> refreshButton.setStyle("-fx-background-color: #cc5f1a; -fx-text-fill: white; -fx-background-radius: 4;"));
        refreshButton.setOnMouseReleased(event -> refreshButton.setStyle("-fx-background-color: #ff791f; -fx-text-fill: white; -fx-background-radius: 4;"));



        // Set up chart styling
        setupChartStyling();

        // Load all data
        loadTopLikedProducts();
        loadStatistics();
        loadRatingStatistics();
        // Initialize Google Calendar Service

//        try {
//            initGoogleCalendar();
//        } catch (Exception e) {
//            System.err.println("Failed to initialize Google Calendar: " + e.getMessage());
//            e.printStackTrace();
//        }

    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void showpopresume() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/resume_aujourd'hui.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ajouter un produit");
            popupStage.setScene(new Scene(root));

            // Add a listener to refresh the dashboard when the popup is closed
            popupStage.setOnHidden(event -> refreshDashboard());

            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }
    //*********************************************************
    @FXML
    public void refreshDashboard() {
        loadTopLikedProducts();
        loadStatistics();
        loadRatingStatistics();
    }


    private void setupChartStyling() {
        // Set chart styling properties
        ratingChart.setTitle("");
        ratingChart.setLabelsVisible(false);
        ratingChart.setLegendVisible(true);

        // Apply CSS styling
        ratingChart.setStyle("-fx-font-family: 'System'; -fx-font-size: 14px;");
    }

    private void loadTopLikedProducts() {
        try {
            List<Integer> topProductIds = likedProductService.getTopLikedProductsByShopId(session.getCurrentUser().getId()); // Use current user's ID
            System.out.println("***********************************"+topProductIds);
            topProductsPane.getChildren().clear();

            if (topProductIds.isEmpty()) {
                emptyStatePane.setVisible(true);
                topProductsPane.setVisible(false);
            } else {
                emptyStatePane.setVisible(false);
                topProductsPane.setVisible(true);

                for (int productId : topProductIds) {
                    try {
                        Produit produit = productService.getEntityById(productId);
                        System.out.println(produit + "uuuuuuuuh");

                        if (produit != null) {
                            topProductsPane.getChildren().add(createProductCard(produit));
                        }
                    } catch (SQLException e) {
                        System.err.println("Error loading product with ID: " + productId);
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading top liked products");
            e.printStackTrace();
            emptyStatePane.setVisible(true);
            topProductsPane.setVisible(false);
        }
    }


    private void loadStatistics() {
        try {
            // Load product count
            int totalProducts = productService.countProductsByShopId(session.getCurrentUser().getId()); // Use current user's ID
            totalProductsLabel.setText(String.valueOf(totalProducts));

            // Load likes count
            int totalLikes = likedProductService.countTotalLikesByShopId(session.getCurrentUser().getId()); // Use current user's ID
            totalLikesLabel.setText(String.valueOf(totalLikes));

            // Calculate average rating
            double avgRating = feedbackService.getAverageRatingByShopId(session.getCurrentUser().getId()); // Use current user's ID
            DecimalFormat df = new DecimalFormat("#.0");
            avgRatingLabel.setText(df.format(avgRating));
        } catch (SQLException e) {
            System.err.println("Error loading statistics");
            e.printStackTrace();
            totalProductsLabel.setText("0");
            totalLikesLabel.setText("0");
            avgRatingLabel.setText("0.0");
        }
    }

    private void loadRatingStatistics() {
        try {
            Map<Integer, Double> ratingStats = feedbackService.getRatingDistribution();

            // Clear previous data
            ratingChart.getData().clear();
            ratingStatsContainer.getChildren().clear();

            // Create data for pie chart with proper colors
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // Total value for percentage calculation
            double total = ratingStats.values().stream().mapToDouble(Double::doubleValue).sum();

            int colorIndex = 0;
            VBox legendContainer = new VBox(15);
            legendContainer.setAlignment(Pos.CENTER);

            for (Map.Entry<Integer, Double> entry : ratingStats.entrySet()) {
                int rating = entry.getKey();
                double value = entry.getValue();
                double percentage = (total > 0) ? (value / total) * 100 : 0;

                // Add data to pie chart with percentage in the label
                PieChart.Data slice = new PieChart.Data(rating + "★ (" + String.format("%.1f", percentage) + "%)", value);
                pieChartData.add(slice);

                // Create legend entry with star and color
                HBox legendEntry = new HBox(10);
                Rectangle colorBox = new Rectangle(15, 15, Color.web(PASTEL_COLORS[colorIndex % PASTEL_COLORS.length]));
                Label starLabel = new Label(rating + "★");
                legendEntry.getChildren().addAll(colorBox, starLabel);
                legendContainer.getChildren().add(legendEntry);

                colorIndex++;
            }

            // Set the data to the chart
            ratingChart.setData(pieChartData);

            // Apply custom colors to the pie chart slices
            applyCustomColorsToChart(pieChartData);

            // Display percentages on top of the pie chart slices
            ratingChart.setLabelsVisible(true);
            ratingChart.setLabelLineLength(10);
            ratingChart.setStartAngle(90); // Rotate the chart for better visibility

            // Remove the default legend
            ratingChart.setLegendVisible(false);

            // Make the pie chart larger
            ratingChart.setPrefSize(400, 400); // Adjust the size as needed

            // Add legend to the stats container
            ratingStatsContainer.getChildren().add(legendContainer);
        } catch (SQLException e) {
            System.err.println("Error loading rating statistics");
            e.printStackTrace();

            // Add an error message to the stats container
            Label errorLabel = new Label("Impossible de charger les statistiques d'évaluation");
            errorLabel.setTextFill(Color.RED);
            ratingStatsContainer.getChildren().add(errorLabel);
        }
    }

    private void applyCustomColorsToChart(ObservableList<PieChart.Data> pieChartData) {
        // Apply pastel colors to pie chart slices
        int colorIndex = 0;
        for (PieChart.Data data : pieChartData) {
            String color = PASTEL_COLORS[colorIndex % PASTEL_COLORS.length];
            String style = String.format("-fx-pie-color: %s;", color);
            data.getNode().setStyle(style);

            // Customize the label to show the percentage on top of the slice
            data.getNode().setOnMouseEntered(event -> {
                data.getNode().setScaleX(1.1);
                data.getNode().setScaleY(1.1);
            });
            data.getNode().setOnMouseExited(event -> {
                data.getNode().setScaleX(1.0);
                data.getNode().setScaleY(1.0);
            });

            colorIndex++;
        }
    }

    private static final String[] PASTEL_COLORS = {
            "#FFDDC1", "#FFABAB", "#FFC3A0", "#D5AAFF", "#85E3FF", "#B9FBC0"
    };


    private VBox createProductCard(Produit produit) {
        VBox card = null;
        try {
            ProduitController produitController = new ProduitController();
            card = produitController.createProductCard(produit);

            // Apply consistent styling to product cards
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 1);");
            card.setPadding(new Insets(15));
            card.setSpacing(10);
            card.setPrefWidth(280);

        } catch (Exception e) {
            System.err.println("Error creating product card for product ID: " + produit.getId());
            e.printStackTrace();

            // Create a simple fallback card on error
            card = new VBox();
            card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-radius: 8;");
            card.setPadding(new Insets(15));
            card.setAlignment(Pos.CENTER);

            Label errorLabel = new Label("Impossible de charger le produit");
            errorLabel.setTextFill(Color.RED);
            card.getChildren().add(errorLabel);
        }

        return card;
    }


    private String getColorForRating(int rating) {
        return CHART_COLORS[(rating - 1) % CHART_COLORS.length];


    }


}