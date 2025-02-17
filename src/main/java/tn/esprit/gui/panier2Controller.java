package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import tn.esprit.entites.Commande;
import tn.esprit.entites.Panier;
import tn.esprit.services.CommandeService;
import tn.esprit.services.panierService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class panier2Controller {

    @FXML
    private VBox cardsContainer;

    @FXML
    private Label totalLabel;

    private int idClient;

    panierService panierService = new panierService();
   CommandeService commandeService = new CommandeService();
    public void setIdClient(int idClient) {
        this.idClient = idClient;
        loadPanier();
    }

    private void loadPanier() {
        try {
            List<Panier> paniers = panierService.showAllClientPanier(idClient);
            if (paniers != null) {
                for (Panier panier : paniers) {
                    HBox card = createCard(panier);
                    cardsContainer.getChildren().add(card);
                }
            }
            updateTotal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createCard(Panier panier) throws SQLException {
        HBox card = new HBox(10);


        Label nameLabel = new Label(panier.getNomProduit());
        Label descriptionLabel = new Label(panier.getDescription());
        Label quantityLabel = new Label(String.valueOf(panier.getQuantite()));

        Button increaseButton = new Button("+");
        increaseButton.setOnAction(event -> {
            try {
                int newQuantity = panierService.augmenterQuantite(panier.getIdCommande(), panier.getIdProduit());
                quantityLabel.setText(String.valueOf(newQuantity));
                updateTotal();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button decreaseButton = new Button("-");
        decreaseButton.setOnAction(event -> {
            try {
                int newQuantity = panierService.diminuerQuantite(panier.getIdCommande(), panier.getIdProduit());
                quantityLabel.setText(String.valueOf(newQuantity));
                updateTotal();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(event -> {
            try {
                boolean deleted = panierService.deletePanier(panier.getIdCommande(), panier.getIdProduit());
                if (deleted) {
                    cardsContainer.getChildren().remove(card);
                    updateTotal();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        card.getChildren().addAll(nameLabel, descriptionLabel, quantityLabel, increaseButton, decreaseButton, deleteButton);
        return card;
    }

    private void updateTotal() {
        try {
            Commande commande = commandeService.getCommandeEnCours(idClient);
            if (commande != null) {
                totalLabel.setText("Total: " + commande.getTotal());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void payerCommande() {
        try {
            commandeService.payerCommande(idClient);
            updateTotal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
