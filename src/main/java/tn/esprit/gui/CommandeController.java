package tn.esprit.gui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tn.esprit.entites.Commande;
import tn.esprit.entites.Panier;
import tn.esprit.services.CommandeService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CommandeController {
    @FXML
    private TableView<Commande> tableView;

    @FXML
    private TableColumn<Commande, Integer> numeroTicketCol;

    @FXML
    private TableColumn<Commande, String> nomClientCol;

    @FXML
    private TableColumn<Commande, Double> totalCommandeCol;

    @FXML
    private TableColumn<Commande, String> dateCommandeCol;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Pagination pagination;

    private CommandeService commandeService = new CommandeService();
    private ObservableList<Commande> commandesList = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 10; // Nombre de commandes par page

    @FXML
    public void initialize() {
        // Fixer la taille du tableau
        tableView.setPrefHeight(400); // Hauteur du tableau
        tableView.setPrefWidth(600); // Largeur du tableau

        // Initialisation des colonnes
        numeroTicketCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getNumeroTicket()).asObject());
        nomClientCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNomClient()));
        totalCommandeCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());
        dateCommandeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateCommande()));

        // Définir la date par défaut sur aujourd'hui
        datePicker.setValue(LocalDate.now());

        // Charger les commandes du jour actuel
        loadCommandes(LocalDate.now().toString());

        // Écouter les changements de date
        datePicker.setOnAction(event -> filterCommandes());

        // Double-clic sur une commande pour voir les détails du panier
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Commande selectedCommande = tableView.getSelectionModel().getSelectedItem();
                if (selectedCommande != null) {
                    showPanierDetails(selectedCommande.getPaniers());
                }
            }
        });
    }

    private void loadCommandes(String date) {
        try {
            int shopId = 3; // ID du shopOwner (à remplacer par une valeur dynamique si nécessaire)
            List<Commande> commandes = commandeService.getCommandesPayeesselonJourPourShopOwner(shopId, date);
            commandesList.setAll(commandes);
            setupPagination();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterCommandes() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            loadCommandes(selectedDate.toString());
        }
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) commandesList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, commandesList.size());

        tableView.setItems(FXCollections.observableArrayList(commandesList.subList(fromIndex, toIndex)));

        VBox box = new VBox();
        box.getChildren().add(tableView);
        return box;
    }

    private void showPanierDetails(List<Panier> paniers) {
        if (paniers == null || paniers.isEmpty()) {
            return;
        }

        VBox vbox = new VBox();
        for (Panier panier : paniers) {
            Label label = new Label(String.format("Produit: %s, Quantité: %d, Prix: %.2f",
                    panier.getNomProduit(), panier.getQuantite(), panier.getPrix()));
            vbox.getChildren().add(label);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Panier");
        alert.setHeaderText("Détails des paniers pour la commande sélectionnée");
        alert.getDialogPane().setContent(vbox);
        alert.showAndWait();
    }
}
