package tn.esprit.gui.mariahosscontroller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label label;

    @FXML
    private void handleButtonClick() {
        label.setText("Button Clicked!");
    }
}
