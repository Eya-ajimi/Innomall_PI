package tn.esprit.gui.oussemacontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entities.Event;
import tn.esprit.entities.EventClient;
import tn.esprit.services.OussemaService.EventClientService;
import tn.esprit.services.OussemaService.QRCodeGenerator;
import tn.esprit.utils.Session;
import tn.esprit.entities.Utilisateur;

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

    public void setEvent(Event event) {
        this.event = event;
        eventName.setText(event.getNomOrganisateur());
        eventDescription.setText(event.getDescription());
        eventDates.setText(event.getDateDebut() + " - " + event.getDateFin());
        eventLocation.setText(event.getEmplacement());

        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser != null) {
            int clientId = currentUser.getId();

            if (eventClientService.isParticipating(clientId, event.getId())) {
                participateButton.setText("Joined");
                participateButton.setDisable(true);
            } else {
                participateButton.setText("Je participe");
                participateButton.setDisable(false);
            }
        } else {
            // Handle the case where there is no current user (e.g., user not logged in)
            participateButton.setText("Login to Participate");
            participateButton.setDisable(true);
        }
    }

    @FXML
    private void handleParticipate() {
        // Get the current user from the session
        Session session = Session.getInstance();
        Utilisateur currentUser = session.getCurrentUser();

        if (currentUser == null) {
            // Handle the case where there is no current user (e.g., user not logged in)
            System.out.println("User not logged in.");
            return;
        }

        int clientId = currentUser.getId();

        EventClient eventClient = new EventClient(clientId, event.getId(), LocalDate.now().toString());
        eventClientService.addParticipation(eventClient);

        participateButton.setText("Joined");
        participateButton.setDisable(true);

        String qrData = "\n Shop Name: " + event.getNomOrganisateur() +
                "\n Description" + event.getDescription() +
                "\n DateDebut: " + event.getDateDebut() +
                "\n DateFin: " + event.getDateFin();

        System.out.println(" QR Code généré avec les données suivantes : \n" + qrData);

        // Generate the QR Code and display it in the existing ImageView
        Image qrImage = QRCodeGenerator.generateQRCode(qrData, 150, 150);

        if (qrImage != null) {
            qrImageView.setImage(qrImage);
        } else {
            System.err.println("️ Erreur : Impossible de générer le QR Code !");
        }
    }
}