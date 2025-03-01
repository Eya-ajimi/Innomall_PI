package tn.esprit.listeners;

import tn.esprit.events.DiscountEvent;
import tn.esprit.services.DiscountNotifier;
import java.util.List;
import java.util.List;

public class EmailNotificationListener implements DiscountListener {
    private List<String> userEmails; // Liste des e-mails des utilisateurs

    public EmailNotificationListener(List<String>userEmails) {
        this.userEmails = userEmails;
    }

    @Override
    public void onDiscountCreated(DiscountEvent event) {
        String subject = "YEEEY Nouvelle Réduction chez Innomall !!!";
        String body = "Détails de la réduction : " +
                "Shop ID: " + event.getShopId() + ", " +
                "Réduction: " + event.getDiscountPercentage() + "%, " +
                "Du " + event.getStartDate() + " au " + event.getEndDate();

        // Envoyer un e-mail à tous les utilisateurs
        DiscountNotifier.notifyUsers(userEmails, subject, body);
    }
}