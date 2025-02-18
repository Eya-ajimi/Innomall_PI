package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tn.esprit.entites.Event;
import tn.esprit.entites.EventClient;
import tn.esprit.services.EventClientService;

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
    private void handleParticipate() {
        EventClient eventClient = new EventClient(clientId, event.getId(), LocalDate.now().toString());
        eventClientService.addParticipation(eventClient);
        participateButton.setText("Joined");
        participateButton.setDisable(true);
    }
}