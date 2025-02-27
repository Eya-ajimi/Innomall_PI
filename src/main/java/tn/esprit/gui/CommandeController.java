package tn.esprit.gui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entites.Commande;
import tn.esprit.entites.Panier;
import tn.esprit.services.CommandeService;

import java.io.IOException;
import java.sql.SQLException;
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
    private static final int ROWS_PER_PAGE = 5;

    @FXML
    public void initialize() {
        datePicker.setValue(java.time.LocalDate.now());

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

        loadCommandes(datePicker.getValue().toString());

        datePicker.setOnAction(event -> filterCommandes());

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Commande selectedCommande = tableView.getSelectionModel().getSelectedItem();
                if (selectedCommande != null) {
                    openPanierPopup(selectedCommande);
                }
            }
        });
    }

    private void loadCommandes(String date) {
        try {
            int shopId = 3;
            List<Commande> commandes = commandeService.getCommandesPayeesselonJourPourShopOwner(shopId, date);
            System.out.println(commandes);

            // 🔴 Correction : Vérifier si la liste est remplie
            System.out.println("Commandes récupérées : " + commandes.size());

            commandesList.setAll(commandes);
            tableView.setItems(commandesList);  // ✅ S'assurer que le TableView est mis à jour

            setupPagination();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterCommandes() {
        loadCommandes(datePicker.getValue().toString());
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

        if (fromIndex >= commandesList.size()) {
            return new VBox();
        }

        ObservableList<Commande> pageData = FXCollections.observableArrayList(commandesList.subList(fromIndex, toIndex));
        tableView.setItems(pageData);

        // Ajuster la hauteur du TableView en fonction du nombre de lignes réelles
        tableView.setPrefHeight(pageData.size() * 30 + 30); // 30 est une estimation de la hauteur d'une ligne

        VBox box = new VBox(tableView);
        return box;
    }

    private void openPanierPopup(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PanierPopup.fxml"));
            VBox root = loader.load();

            PanierPopupController popupController = loader.getController();
            popupController.setData(commande, this);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Détails du Panier");
            popupStage.setScene(new Scene(root));
            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() {
        loadCommandes(datePicker.getValue().toString());
    }
}
