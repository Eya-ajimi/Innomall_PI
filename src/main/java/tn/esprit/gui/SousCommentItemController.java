package tn.esprit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tn.esprit.entites.SousCommentaire;

public class SousCommentItemController {

        @FXML
        private Label userIdLabel;
        @FXML
        private Label contentLabel;
        @FXML
        private Label timeLabel;

        public void setSousCommentaire(SousCommentaire sousCommentaire) {
            // Make sure you are setting the correct data here
            userIdLabel.setText(String.valueOf(sousCommentaire.getUtilisateurId()));
            contentLabel.setText(sousCommentaire.getContenu());
            timeLabel.setText(sousCommentaire.getDateCreation());  // Make sure date format is correct
        }
    }


