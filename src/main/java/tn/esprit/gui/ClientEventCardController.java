package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entites.Event;
import tn.esprit.entites.EventClient;
import tn.esprit.services.EventClientService;
import tn.esprit.services.QRCodeGenerator;
import javafx.scene.image.ImageView; // ✅ Correct JavaFX import
import java.awt.*;
import java.time.LocalDate;

public class ClientEventCardController {

    @FXML
    private Label eventName;

    @FXML
    private Label eventDescription;

    @FXML
    private Label eventDates;

    @FXML
    private Label eventLocation;

    @FXML
    private Button participateButton;

    @FXML
    private ImageView qrImageView;

    private Event event;
    private EventClientService eventClientService = new EventClientService();
    private int clientId = 1; // Replace with the actual client ID

    public void setEvent(Event event) {
        this.event = event;
        eventName.setText(event.getNomOrganisateur());
        eventDescription.setText(event.getDescription());
        eventDates.setText(event.getDateDebut() + " - " + event.getDateFin());
        eventLocation.setText(event.getEmplacement());

        if (eventClientService.isParticipating(clientId, event.getId())) {
            participateButton.setText("Joined");
            participateButton.setDisable(true);
        } else {
            participateButton.setText("Je participe");
            participateButton.setDisable(false);
        }
    }

    @FXML
    private ImageView QRimg; // ️ Vérifie que ton ImageView est bien lié au FXML

    @FXML
    private void handleParticipate() {
        EventClient eventClient = new EventClient(clientId, event.getId(), LocalDate.now().toString());
        eventClientService.addParticipation(eventClient);

        participateButton.setText("Joined");
        participateButton.setDisable(true);

        String qrData = "\n Shop Name: " + event.getNomOrganisateur() +
                "\n Description" + event.getDescription() +
                "\n DateDebut: " + event.getDateDebut() +
                "\n DateFin: " + event.getDateFin();

        System.out.println(" QR Code généré avec les données suivantes : \n" + qrData);

        // ✅ Générer le QR Code et l'afficher dans l'ImageView existant
        Image qrImage = QRCodeGenerator.generateQRCode(qrData, 150, 150);

        if (qrImage != null) {
            qrImageView.setImage(qrImage); // ✅ Maintenant compatible avec JavaFX
        } else {
            System.err.println("️ Erreur : Impossible de générer le QR Code !");
        }
    }

}