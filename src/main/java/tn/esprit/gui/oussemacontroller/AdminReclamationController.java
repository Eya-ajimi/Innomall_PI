package tn.esprit.gui.oussemacontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Reclamation;
import tn.esprit.services.OussemaService.ReclamationService;

public class AdminReclamationController {
    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, String> descriptionColumn;
    @FXML private TableColumn<Reclamation, Void> actionColumn;

    private ReclamationService reclamationService = new ReclamationService();

    @FXML
    private void initialize() {
        // Set up the description column
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Set up the action column with an "Update" button
        actionColumn.setCellFactory(new Callback<TableColumn<Reclamation, Void>, TableCell<Reclamation, Void>>() {
            @Override
            public TableCell<Reclamation, Void> call(final TableColumn<Reclamation, Void> param) {
                return new TableCell<Reclamation, Void>() {
                    private final Button btn = new Button("Reply");

                    {
                        btn.getStyleClass().add("reply-button"); // Apply the CSS class
                        btn.setMaxWidth(Double.MAX_VALUE); // Make the button fill the cell width
                        btn.setOnAction(event -> {
                            Reclamation reclamation = getTableView().getItems().get(getIndex());
                            openUpdateWindow(reclamation);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                            setAlignment(Pos.CENTER); // Center the button in the cell
                        }
                    }
                };
            }
        });

        // Load data into the table
        try {
            reclamationTable.getItems().addAll(reclamationService.getAllReclamations());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openUpdateWindow(Reclamation reclamation) {
        try {
            // Load the FXML file for the pop-up window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/update_reclamation.fxml"));
            AnchorPane root = loader.load();

            // Get the controller and set the reclamation object
            UpdateReclamationController controller = loader.getController();
            controller.setReclamation(reclamation);

            // Create a new stage for the pop-up window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Reclamation");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}