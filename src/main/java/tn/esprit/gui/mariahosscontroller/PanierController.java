package tn.esprit.gui.mariahosscontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Commande;
import tn.esprit.entities.Panier;
import tn.esprit.services.mariahossservice.CommandeService;
import tn.esprit.services.mariahossservice.panierService;
import tn.esprit.utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PanierController {

    @FXML
    private VBox panierVBox;
    @FXML
    private Label homeLabel;
    @FXML
    private Label totalLabel;


    private panierService panierService = new panierService();
    private CommandeService commandeService = new CommandeService();

    Session session = Session.getInstance();
    private int idClient = session.getCurrentUser().getId(); // ID du client connecté (à initialiser)

    @FXML
    public void initialize() {
        try {
            List<Panier> paniers = panierService.showAllClientPanier(idClient);
            if (paniers != null) {
                for (Panier panier : paniers) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PanierCard.fxml"));
                    AnchorPane card = loader.load();
                    PanierCardController cardController = loader.getController();

                    cardController.setPanier(panier);
                    cardController.setPanierController(PanierController.this);

                    // Mettre à jour les labels
                    cardController.nomLabel.setText(panier.getNomProduit());
                    cardController.descriptionLabel.setText(panier.getDescription());
                    cardController.quantiteLabel.setText(String.valueOf(panier.getQuantite()));
                    cardController.prixLabel.setText(String.format("%.2f dt", panier.getPrix()));

                    panierVBox.getChildren().add(card);
                }
            }
            // Add event handler to the Home label
            homeLabel.setOnMouseClicked(event -> {
                try {
                    // Load the Homepage.fxml file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Homepage.fxml"));
                    Parent root = loader.load();

                    // Get the current stage (window)
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    // Set the new scene
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            updateTotal();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Mettre à jour le total de la commande
    private void updateTotal() throws SQLException {
        Commande commande = commandeService.getCommandeEnCours(idClient);
        if (commande != null) {
            totalLabel.setText(String.format("%.2f dt", commande.getTotal()));
        } else {
            // Si la commande est null, affichez un total de 0.00 dt
            totalLabel.setText("0.00 dt");
        }
    }

    // Gérer l'augmentation de la quantité
    @FXML
    void handleAugmenterQuantite(Panier panier) throws SQLException, IOException {
        int newQuantite = panierService.augmenterQuantite(panier.getIdCommande(), panier.getIdProduit());
        double prixProduit = panierService.getProduitById(panier.getIdProduit()).getPrix();
        if (newQuantite > 0) {
            panier.setQuantite(newQuantite);
            panier.setPrix(prixProduit * newQuantite);
            updatePanierCards();
            updateTotal();
        }
    }

    // Gérer la diminution de la quantité
    @FXML
    void handleDiminuerQuantite(Panier panier) throws SQLException, IOException {
        int newQuantite = panierService.diminuerQuantite(panier.getIdCommande(), panier.getIdProduit());
        double prixProduit = panierService.getProduitById(panier.getIdProduit()).getPrix();
        if (newQuantite > 0) {
            panier.setQuantite(newQuantite);
            panier.setPrix(prixProduit * newQuantite);
            updatePanierCards();
            updateTotal();
        }
    }

    // Gérer la suppression d'un article
    @FXML
    void handleSupprimerArticle(Panier panier) throws SQLException, IOException {
        boolean isDeleted = panierService.deletePanier(panier.getIdCommande(), panier.getIdProduit());
        if (isDeleted) {
            updatePanierCards();
            updateTotal();
        }
    }

    // Gérer le passage de la commande
    private int nbClicks = 0; // Variable de classe pour compter les clics

    @FXML
    private void handlePasserCommande() throws SQLException {
        commandeService.payerCommande(idClient);
        Commande cmd = commandeService.getCommandeEnCours(idClient);

        if (cmd == null && nbClicks == 0) {
            nbClicks++; // Incrémenter le compteur de clics
            updateTotal();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Commande passée avec succès !");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Commande déja passé !");
            alert.showAndWait();
        }}

    // Mettre à jour les cartes du panier
    private void updatePanierCards() throws SQLException, IOException {
        panierVBox.getChildren().clear();
        List<Panier> paniers = panierService.showAllClientPanier(idClient);
        if (paniers != null) {
            for (Panier panier : paniers) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/panierCard.fxml"));
                HBox card = loader.load();
                PanierCardController cardController = loader.getController();

                cardController.setPanier(panier);
                cardController.setPanierController(PanierController.this);

                // Mettre à jour les labels
                cardController.nomLabel.setText(panier.getNomProduit());
                cardController.descriptionLabel.setText(panier.getDescription());
                cardController.quantiteLabel.setText(String.valueOf(panier.getQuantite()));
                cardController.prixLabel.setText(String.format("%.2f dt", panier.getPrix()));

                panierVBox.getChildren().add(card);
            }
        }
    }
}