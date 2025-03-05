package tn.esprit.gui.mariahosscontroller;

import javax.mail.MessagingException;
import java.io.File;

import com.google.api.services.gmail.Gmail;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.services.mariahossservice.*;
import tn.esprit.utils.GmailServiceFactory;
import tn.esprit.utils.Session;
import tn.esprit.entities.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import tn.esprit.entities.enums.StatutCommande;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResumeAujourdHuiController {

    @FXML
    private Button exportPdf;
    @FXML
    private Button cancelButton;
    @FXML
    private Label commandesPayeesLabel;
    @FXML
    private Label totalCommandesLabel;
    @FXML
    private VBox commandesEncoursBox;
    @FXML
    private VBox soonOutOfStockBox;
    @FXML
    private VBox outOfStockBox;
    @FXML
    private Label averageFeedbackLabel;
    @FXML
    private Label bestSellingProductLabel;

    private Gmail gmailService; // Gmail service instance
    private EmailSender emailSender; // EmailSender instance

    private final panierService panierService = new panierService();
    private final CommandeService commandeService = new CommandeService();
    private final ProductService productservice = new ProductService();
    private FeedbackService feedbackService = new FeedbackService();
    private final Session session = Session.getInstance();

    public void initialize() {

        // Initialize Gmail service and EmailSender
        try {
            gmailService = initializeGmailService(); // Initialize Gmail service
            emailSender = new EmailSender(gmailService); // Initialize EmailSender
        } catch (IOException e) {
            showAlert("Erreur lors de l'initialisation du service Gmail: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        exportPdf.setOnAction(event -> exportPdf());
        cancelButton.setOnAction(event -> closePopup());

        // Date d'aujourd'hui
        String dateAujourdhui = LocalDate.now().toString();
        int shopId = session.getCurrentUser().getId();
        try {
            // Récupérer les commandes payées
            int nombreCommandesPayees = commandeService.getNombreCommandesPayees(shopId, dateAujourdhui);
            commandesPayeesLabel.setText("Commandes payées aujourd'hui : " + nombreCommandesPayees);

            // Récupérer le total des ventes
            double totalCommandes = commandeService.getTotalCommandesPayees(shopId, dateAujourdhui);
            totalCommandesLabel.setText("Total des ventes aujourd'hui : " + totalCommandes + " DT");

            // Récupérer et afficher les commandes "enCours"
            List<Commande> commandesEncours = commandeService.getCommandesEnCoursByShop(shopId);
            displayCommandesEncours(commandesEncours != null ? commandesEncours : new ArrayList<>());

            // Fetch products out of stock
            List<Produit> outOfStockProducts = productservice.getProductsOutOfStock(shopId);
            System.out.println("Out of stock products: " + outOfStockProducts);

            // Fetch products soon out of stock
            List<Produit> soonOutOfStockProducts = productservice.getProductsSoonOutOfStock(shopId);
            System.out.println("Soon out of stock products: " + soonOutOfStockProducts);


            // Display the products in the UI
            displayProducts(soonOutOfStockProducts, soonOutOfStockBox, "Produits bientôt en rupture");
            displayProducts(outOfStockProducts, outOfStockBox, "Produits en rupture de stock");
            double average = feedbackService.getAverageRatingByShopId(shopId);
            averageFeedbackLabel.setText(String.format("Moyenne feedbacks aujourd'hui: %.2f", average));

            Produit bestProduct = panierService.getBestSelledProductToday(shopId);
            if (bestProduct != null) {
                showAlert("Meilleur produit vendu aujourd'hui: " + bestProduct.getNom(), Alert.AlertType.INFORMATION);
            } else {
                showAlert("Aucune vente enregistrée aujourd'hui.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Erreur lors du chargement des commandes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void displayProducts(List<Produit> produits, VBox container, String defaultMessage) {
        container.getChildren().clear();
        if (produits.isEmpty()) {
            container.getChildren().add(new Label(defaultMessage + " : Aucun produit trouvé."));
        } else {
            for (Produit produit : produits) {
                Label productLabel = new Label(produit.getNom() + " - Stock: " + produit.getStock());
                container.getChildren().add(productLabel);
            }
        }
    }

    private void displayCommandesEncours(List<Commande> commandes) {
        commandesEncoursBox.getChildren().clear(); // Clear previous data

        if (commandes.isEmpty()) {
            commandesEncoursBox.getChildren().add(new Label("Aucune commande en cours."));
        } else {
            for (Commande commande : commandes) {
                Label commandeLabel = new Label("Commande #" + commande.getId() + " - Total: " + commande.getTotal() + " DT");
                commandesEncoursBox.getChildren().add(commandeLabel);
            }
        }
    }

    private void closePopup() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Succès" : "Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void exportPdf() {
        int shopId = session.getCurrentUser().getId();
        try {
            String pdfPath = generatePopupContentPDF(shopId); // Generate PDF
            sendResumeByEmail(shopId, gmailService, pdfPath); // Send email with PDF

            showAlert("PDF exporté et envoyé avec succès!", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Erreur lors de l'exportation du PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String generatePopupContentPDF(int shopId) throws IOException {
        String filePath = "resume_popup.pdf";
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set up margins and initial position
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float yPosition = yStart;

                // Add Title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Résumé du jour - Shop ID: " + shopId);
                contentStream.endText();

                // Add a separator line below the title
                yPosition -= 20;
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(margin + tableWidth, yPosition);
                contentStream.stroke();

                // Set font for body text
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                yPosition -= 20;

                // Add Commandes Payées
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(commandesPayeesLabel.getText());
                contentStream.endText();
                yPosition -= 15;

                // Add Total des Ventes
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(totalCommandesLabel.getText());
                contentStream.endText();
                yPosition -= 15;

                // Add Moyenne Feedbacks
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(averageFeedbackLabel.getText());
                contentStream.endText();
                yPosition -= 20;

                // Add a table for Produits Bientôt en Rupture
                yPosition = addTable(contentStream, margin, yPosition, tableWidth, "Produits Bientôt en Rupture", soonOutOfStockBox);

                // Add a table for Produits en Rupture de Stock
                yPosition = addTable(contentStream, margin, yPosition, tableWidth, "Produits en Rupture de Stock", outOfStockBox);

                // Add a table for Commandes en Cours
                yPosition = addTable(contentStream, margin, yPosition, tableWidth, "Commandes en Cours", commandesEncoursBox);
            }

            document.save(filePath);
        }
        return filePath;
    }

    private float addTable(PDPageContentStream contentStream, float margin, float yPosition, float tableWidth, String title, VBox dataBox) throws IOException {
        // Add Table Title
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        yPosition -= 15;

        // Draw Table Header
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Nom");
        contentStream.newLineAtOffset(tableWidth / 2, 0);
        contentStream.showText("Stock / Total");
        contentStream.endText();
        yPosition -= 15;

        // Draw Table Rows
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        for (javafx.scene.Node node : dataBox.getChildren()) {
            if (node instanceof Label) {
                String text = ((Label) node).getText();
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(text);
                contentStream.endText();
                yPosition -= 15;
            }
        }

        // Add space after the table
        yPosition -= 10;
        return yPosition;
    }

    private void sendResumeByEmail(int shopId, Gmail gmailService, String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            emailSender.sendEmailWithAttachment(
                    gmailService,
                    "ammarim073@gmail.com",
                    "Résumé des ventes aujourd'hui",
                    "Bonjour,\n\nVeuillez trouver ci-joint le résumé des ventes du jour.",
                    pdfFile
            );
        } catch (IOException | MessagingException e) {
            showAlert("Erreur lors de l'envoi de l'email: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private Gmail initializeGmailService() throws IOException {
        try {
            return GmailServiceFactory.getGmailService(); // Use GmailServiceFactory to initialize Gmail service
        } catch (Exception e) {
            throw new IOException("Failed to initialize Gmail service: " + e.getMessage(), e);
        }
    }
}