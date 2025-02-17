package tn.esprit.gui;

//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.geometry.Insets;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import tn.esprit.entites.Commande;
//import tn.esprit.entites.Panier;
//import tn.esprit.services.CommandeService;
//import tn.esprit.services.panierService;
//
//import java.net.URL;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.ResourceBundle;

public class panierController  {

//    @FXML
//    private VBox itemsContainer;
//    @FXML
//    private Label totalLabel;
//    @FXML
//    private Button payerButton;
//
//    private final panierService panierService = new panierService();
//    private final CommandeService commandeService = new CommandeService();
//    private int currentClientId = 1; // Remplacer par l'ID réel
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        setupPanier();
//        setupPayerButton();
//    }
//
//    private void setupPanier() {
//        itemsContainer.getChildren().clear();
//        try {
//            List<Panier> items = panierService.showAllClientPanier(currentClientId);
//            if (items != null && !items.isEmpty()) {
//                items.forEach(item -> itemsContainer.getChildren().add(createItemCard(item)));
//            } else {
//                showEmptyPanierMessage();
//            }
//        } catch (SQLException e) {
//            showError("Erreur de chargement", e.getMessage());
//        }
//        updateTotal();
//    }
//
//    private HBox createItemCard(Panier item) {
//        HBox card = new HBox(20);
//        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
//
//        // Image
//        ImageView imageView = createProductImage(item.getIdProduit());
//
//        // Infos produit
//        VBox infoBox = createProductInfo(item);
//
//        // Gestion quantité
//        HBox quantityBox = createQuantityControls(item);
//
//        // Prix
//        Label priceLabel = createPriceLabel(item.getPrix());
//
//        // Bouton suppression
//        Button deleteBtn = createDeleteButton(item);
//
//        card.getChildren().addAll(imageView, infoBox, quantityBox, priceLabel, deleteBtn);
//        card.setPadding(new Insets(10));
//        return card;
//    }
//
//    private ImageView createProductImage(int productId) {
//        ImageView iv = new ImageView();
//        try {
//            Image img = new Image("file:assets/products/" + productId + ".png");
//            iv.setImage(img);
//        } catch (Exception e) {
//            iv.setImage(new Image("file:assets/products/default.png"));
//        }
//        iv.setFitWidth(80);
//        iv.setFitHeight(80);
//        return iv;
//    }
//
//    private VBox createProductInfo(Panier item) {
//        VBox infoBox = new VBox(5);
//        Label nameLabel = new Label(item.getNomProduit());
//        nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
//        Label descLabel = new Label(item.getDescription());
//        infoBox.getChildren().addAll(nameLabel, descLabel);
//        return infoBox;
//    }
//
//    private HBox createQuantityControls(Panier item) {
//        HBox quantityBox = new HBox(10);
//        Button minusBtn = new Button("-");
//        Button plusBtn = new Button("+");
//        Label quantityLabel = new Label(String.valueOf(item.getQuantite()));
//
//        String buttonStyle = "-fx-background-color: #e0e0e0; -fx-min-width: 30px; -fx-font-size: 16px;";
//        minusBtn.setStyle(buttonStyle);
//        plusBtn.setStyle(buttonStyle);
//
//        minusBtn.setOnAction(e -> updateQuantity(item, false));
//        plusBtn.setOnAction(e -> updateQuantity(item, true));
//
//        quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn);
//        return quantityBox;
//    }
//
//    private Label createPriceLabel(double price) {
//        Label priceLabel = new Label(String.format("%.2f DT", price));
//        priceLabel.setStyle("-fx-font-size: 18;");
//        return priceLabel;
//    }
//
//    private Button createDeleteButton(Panier item) {
//        Button btn = new Button("×");
//        btn.setStyle("-fx-text-fill: red; -fx-font-size: 20; -fx-background-color: transparent;");
//        btn.setOnAction(e -> confirmDelete(item));
//        return btn;
//    }
//
//    private void updateQuantity(Panier item, boolean increase) {
//        try {
//            int newQuantity = panierService.updateQuantity(
//                    item.getIdCommande(),
//                    item.getIdProduit(),
//                    increase
//            );
//
//            if (newQuantity > 0) {
//                item.setQuantite(newQuantity);
//                refreshPanier();
//            }
//        } catch (SQLException e) {
//            showError("Erreur de mise à jour", e.getMessage());
//        }
//    }
//
//    private void confirmDelete(Panier item) {
//        // Implémenter une confirmation ici
//        deleteItem(item);
//    }
//
//    private void deleteItem(Panier item) {
//        try {
//            if (panierService.deletePanier(item.getIdCommande(), item.getIdProduit())) {
//                refreshPanier();
//            }
//        } catch (SQLException e) {
//            showError("Erreur de suppression", e.getMessage());
//        }
//    }
//
//    private void refreshPanier() {
//        setupPanier();
//        updateTotal();
//    }
//
//    private void updateTotal() {
//        try {
//            Commande commande = commandeService.getCommandeEnCours(currentClientId);
//            totalLabel.setText(commande != null ?
//                    String.format("%.2f DT", commande.getTotal()) :
//                    "0.00 DT"
//            );
//        } catch (SQLException e) {
//            showError("Erreur de total", e.getMessage());
//            totalLabel.setText("Erreur");
//        }
//    }
//
//    private void setupPayerButton() {
//        payerButton.setOnAction(e -> {
//            try {
//                commandeService.payerCommande(currentClientId);
//                showSuccess("Paiement réussi !");
//                refreshPanier();
//            } catch (SQLException ex) {
//                showError("Erreur de paiement", ex.getMessage());
//            }
//        });
//    }
//
//    private void showEmptyPanierMessage() {
//        Label emptyLabel = new Label("Votre panier est vide");
//        emptyLabel.setStyle("-fx-font-size: 18; -fx-text-fill: gray;");
//        itemsContainer.getChildren().add(emptyLabel);
//    }
//
//    private void showError(String title, String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//    private void showSuccess(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Succès");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
}