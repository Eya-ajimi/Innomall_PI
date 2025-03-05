package tn.esprit.gui.azizcontroller;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import tn.esprit.entities.Role;
import tn.esprit.services.azizservice.UserService;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class StatistiquesControlleraziz implements Initializable {

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private BarChart<String, Number> barChart;

    private UserService utilisateurService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilisateurService = new UserService();

        // Remplir la ComboBox avec les rôles
        roleComboBox.setItems(FXCollections.observableArrayList(Role.CLIENT, Role.SHOPOWNER));
        roleComboBox.getSelectionModel().selectFirst(); // Sélectionner le premier rôle par défaut

        // Charger les statistiques pour le rôle sélectionné
        loadStatistics();

        // Écouter les changements de sélection dans la ComboBox
        roleComboBox.setOnAction(event -> loadStatistics());
    }

    private void loadStatistics() {
        Role selectedRole = roleComboBox.getValue();
        if (selectedRole != null) {
            try {
                // Récupérer les données depuis la base de données
                Map<String, Integer> stats = utilisateurService.getWeeklyRegistrationsByRole(selectedRole);

                // Préparer les données pour le graphique
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Inscriptions par semaine");

                for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }

                // Mettre à jour le graphique
                barChart.getData().clear();
                barChart.getData().add(series);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}