package tn.esprit.gui.mariahosscontroller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Commande;
import tn.esprit.entities.Panier;
import tn.esprit.services.mariahossservice.CommandeService;
import tn.esprit.utils.Session;
import java.sql.SQLException;
import java.util.List;

public class PanierPopupController {
    @FXML
    private VBox panierContainer;

    @FXML
    private Label totalCommandeLabel;  // Ajout du label pour afficher le total

    @FXML
    private Button btnConfirmer;

    private Commande commande;
    private CommandeController parentController;
    private CommandeService commandeService = new CommandeService();
    private Session session = Session.getInstance();

    public void setData(Commande commande, CommandeController parentController) {
        this.commande = commande;
        this.parentController = parentController;
        afficherPaniers();
        afficherTotalCommande();
    }

    private void afficherPaniers() {
        panierContainer.getChildren().clear();

        List<Panier> paniers = commande.getPaniers();
        for (Panier panier : paniers) {
            VBox card = new VBox();
            card.setSpacing(5);
            card.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10px; -fx-border-radius: 5px; -fx-border-color: #ccc;");

            Label nomProduit = new Label("Produit: " + panier.getNomProduit());
            Label quantite = new Label("Quantité: " + panier.getQuantite());
            Label prix = new Label("Prix: " + panier.getPrix() + " DT");

            card.getChildren().addAll(nomProduit, quantite, prix);
            panierContainer.getChildren().add(card);
        }
    }

    private void afficherTotalCommande() {
        totalCommandeLabel.setText("Total Commande: " + commande.getTotal() + " DT");
    }

    @FXML
    private void confirmerCommande() {
        try {
            commandeService.updateStatutCommandeEtPaniers(commande.getId(), session.getCurrentUser().getId());

            parentController.refreshTable();
            fermerFenetre();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnConfirmer.getScene().getWindow();
        stage.close();
    }
}