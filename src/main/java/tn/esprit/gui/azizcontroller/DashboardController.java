package tn.esprit.gui.azizcontroller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import javafx.scene.layout.StackPane;


public class DashboardController {
    @FXML
    private AnchorPane mainPane; // Ensure this matches the fx:id in FXML
    @FXML
    private StackPane contentPane;

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboardContent() {
        loadView("/fxml/DashboardContent.fxml");
    }


    @FXML
    private void showusersmanagement() {
        loadView("/fxml/adminDashboard.fxml");
    }

    @FXML
    private void showreclamationmanagement() {
        loadView("/fxml/GestionReclamation.fxml");
    }

    @FXML
    private void showstatistics() {
        loadView("/fxml/Statiqtiques.fxml");
    }


}
