package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.entites.Commande;
import tn.esprit.entites.Panier;
import tn.esprit.services.CommandeService;
import tn.esprit.services.panierService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PanierController {

    @FXML
    private ListView<Panier> panierListView;

    @FXML
    private Label totalLabel;

    private panierService panierService = new panierService();
    private CommandeService commandeService = new CommandeService();

    private int idClient = 1; // ID du client connecté (à initialiser)

    @FXML
    public void initialize() {
        try {
            panierListView.setCellFactory(param -> new PanierListCell());

            List<Panier> paniers = panierService.showAllClientPanier(idClient);
            if(paniers != null) {
                panierListView.getItems().addAll(paniers);
            }

            updateTotal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mettre à jour le total de la commande
    private void updateTotal() throws SQLException {
        Commande commande = commandeService.getCommandeEnCours(idClient);
        if (commande != null) {
            totalLabel.setText(String.format("%.2f dt", commande.getTotal()));
        } else {
            totalLabel.setText("0.00 €");
        }
    }

    // Gérer l'augmentation de la quantité
    @FXML
    void handleAugmenterQuantite(Panier panier) throws SQLException {
        int newQuantite = panierService.augmenterQuantite(panier.getIdCommande(), panier.getIdProduit());
        if (newQuantite > 0) {
            panier.setQuantite(newQuantite);
            panier.setPrix(panier.getPrix() / panier.getQuantite() * newQuantite); // Recalculer le prix
            panierListView.refresh();
            updateTotal();
        }
    }

    // Gérer la diminution de la quantité
    @FXML
    void handleDiminuerQuantite(Panier panier) throws SQLException {
        int newQuantite = panierService.diminuerQuantite(panier.getIdCommande(), panier.getIdProduit());
        if (newQuantite > 0) {
            panier.setQuantite(newQuantite);
            panier.setPrix(panier.getPrix() / panier.getQuantite() * newQuantite); // Recalculer le prix
            panierListView.refresh();
            updateTotal();
        }
    }

    // Gérer la suppression d'un article
    @FXML
    void handleSupprimerArticle(Panier panier) throws SQLException {
        boolean isDeleted = panierService.deletePanier(panier.getIdCommande(), panier.getIdProduit());
        if (isDeleted) {
            panierListView.getItems().remove(panier);
            updateTotal();
        }
    }

    // Gérer le passage de la commande
    @FXML
    private void handlePasserCommande() throws SQLException {
        commandeService.payerCommande(idClient);
        updateTotal();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Commande passée avec succès !");
        alert.showAndWait();
    }

    // Classe interne pour la cellule personnalisée
    private class PanierListCell extends ListCell<Panier> {
        @Override
        protected void updateItem(Panier panier, boolean empty) {
            super.updateItem(panier, empty);
            if (empty || panier == null) {
                setGraphic(null);
            } else {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/panierCard.fxml"));
                    HBox card = loader.load();
                    PanierCardController cardController = loader.getController();

                    cardController.setPanier(panier);
                    cardController.setPanierController(PanierController.this);

                    // Update labels
                    cardController.nomLabel.setText(panier.getNomProduit());
                    cardController.descriptionLabel.setText(panier.getDescription());
                    cardController.quantiteLabel.setText(String.valueOf(panier.getQuantite()));
                    cardController.prixLabel.setText(String.format("%.2f dt", panier.getPrix()));

                    setGraphic(card);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Failed to load panierCard.fxml. Check the file path and ensure it exists.");
                }
            }
        }
    }
}