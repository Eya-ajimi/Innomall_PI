package tn.esprit.gui.mariahosscontroller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Commande;
import tn.esprit.services.mariahossservice.CommandeService;
import tn.esprit.utils.Session;

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
    private Session session = Session.getInstance();

    private CommandeService commandeService = new CommandeService();
    private ObservableList<Commande> commandesList = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 5;

    @FXML
    public void initialize() {
        // Set the initial date value
        datePicker.setValue(java.time.LocalDate.now());

        // Setup column cell value factories
        numeroTicketCol.setCellValueFactory(new PropertyValueFactory<>("numeroTicket"));
        nomClientCol.setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        totalCommandeCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        dateCommandeCol.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));

        // Load initial data
        loadCommandes(datePicker.getValue().toString());

        // Set event handlers
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
            int shopId =session.getCurrentUser().getId();
            List<Commande> commandes = commandeService.getCommandesPayeesselonJourPourShopOwner(shopId, date);

            System.out.println("Commandes récupérées : " + commandes.size());
            for (Commande cmd : commandes) {
                System.out.println("Commande: " + cmd);
            }

            // Update the observable list with new data
            commandesList.clear();
            commandesList.addAll(commandes);

            // Setup pagination based on the new data
            setupPagination();

            // The table items will be updated by the pagination system
        } catch (SQLException e) {
            e.printStackTrace();
            // Show error alert to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Erreur lors du chargement des commandes");
            alert.setContentText("Une erreur est survenue: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void filterCommandes() {
        if (datePicker.getValue() != null) {
            loadCommandes(datePicker.getValue().toString());
        }
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) commandesList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));

        // Reset to first page when filtering
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);

        // Initial update of table view
        updateTableView(0);
    }

    private VBox createPage(int pageIndex) {
        updateTableView(pageIndex);
        return new VBox(tableView);
    }

    private void updateTableView(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, commandesList.size());

        ObservableList<Commande> pageItems;
        if (fromIndex >= commandesList.size()) {
            pageItems = FXCollections.observableArrayList();
        } else {
            pageItems = FXCollections.observableArrayList(
                    commandesList.subList(fromIndex, toIndex)
            );
        }

        tableView.setItems(pageItems);
        tableView.refresh();
    }

    private void openPanierPopup(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PanierPopup.fxml"));
            Parent root = loader.load();

            PanierPopupController popupController = loader.getController();
            popupController.setData(commande, this);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Détails du Panier");
            popupStage.setScene(new Scene(root));
            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur d'affichage");
            alert.setContentText("Impossible d'ouvrir les détails du panier: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void refreshTable() {
        // Reload data with current date filter
        if (datePicker.getValue() != null) {
            loadCommandes(datePicker.getValue().toString());
        }
    }
}