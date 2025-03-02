package tn.esprit.listeners;

import tn.esprit.events.DiscountEvent;
import tn.esprit.services.mariahossservice.DiscountNotifier;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.List;
import java.util.Locale;

public class EmailNotificationListener implements DiscountListener {
    private List<String> userEmails; // Liste des e-mails des utilisateurs

    public EmailNotificationListener(List<String> userEmails) {
        this.userEmails = userEmails;
    }

    @Override
    public void onDiscountCreated(DiscountEvent event) {
        String subject = "Nouvelle promotion exclusive chez Innomall !";

        // Create an HTML email body with better styling
        String body = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            color: #333333;\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #4a90e2;\n" +
                "            color: white;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            border-radius: 5px 5px 0 0;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "            background-color: #f9f9f9;\n" +
                "            border: 1px solid #dddddd;\n" +
                "            border-top: none;\n" +
                "            border-radius: 0 0 5px 5px;\n" +
                "        }\n" +
                "        .offer-details {\n" +
                "            background-color: #ffffff;\n" +
                "            border: 1px solid #dddddd;\n" +
                "            border-radius: 5px;\n" +
                "            padding: 15px;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .discount-value {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "            color: #e74c3c;\n" +
                "        }\n" +
                "        .dates {\n" +
                "            font-style: italic;\n" +
                "            color: #555555;\n" +
                "        }\n" +
                "        .cta-button {\n" +
                "            display: block;\n" +
                "            width: 200px;\n" +
                "            background-color: #e74c3c;\n" +
                "            color: white;\n" +
                "            text-align: center;\n" +
                "            padding: 12px 0;\n" +
                "            margin: 20px auto;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            font-size: 12px;\n" +
                "            text-align: center;\n" +
                "            color: #777777;\n" +
                "            margin-top: 30px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"header\">\n" +
                "        <h1>Offre Spéciale Innomall</h1>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "        <p>Cher(e) client(e),</p>\n" +
                "        <p>Nous sommes ravis de vous annoncer une nouvelle promotion exclusive chez Innomall !</p>\n" +
                "        \n" +
                "        <div class=\"offer-details\">\n" +
                "            <p><strong>Boutique :</strong> " + event.getShopId() + "</p>\n" +
                "            <p class=\"discount-value\">" + event.getDiscountPercentage() + "% de réduction</p>\n" +
                "            <p class=\"dates\">Valable du " + formatDate(event.getStartDate()) + " au " + formatDate(event.getEndDate()) + "</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <p>Ne manquez pas cette opportunité exceptionnelle de profiter de réductions sur vos produits préférés !</p>\n" +
                "        \n" +
                "        <a href=\"https://innomall.com/promotions?shop=" + event.getShopId() + "\" class=\"cta-button\">Découvrir l'offre</a>\n" +
                "        \n" +
                "        <p>À bientôt sur Innomall !</p>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <p>Cet email a été envoyé à {{email}} car vous êtes inscrit aux notifications de Innomall.</p>\n" +
                "            <p>Pour vous désabonner, <a href=\"https://innomall.com/unsubscribe\">cliquez ici</a>.</p>\n" +
                "            <p>© 2025 Innomall. Tous droits réservés.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        // Envoyer un e-mail à tous les utilisateurs
        DiscountNotifier.notifyUsers(userEmails, subject, body,true); // Added boolean parameter to indicate HTML format
    }

    // Helper method to format dates in a more user-friendly way
    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("fr", "FR"));
        return outputFormat.format(date);
    }

}