package tn.esprit.tests;
import tn.esprit.entities.Discount;
import tn.esprit.listeners.EmailNotificationListener;
import tn.esprit.services.mariahossservice.DiscountService;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class testmailmaria {

    public static void main(String[] args) {
        // Exemple de liste d'e-mails des utilisateurs
        List<String> userEmails = Arrays.asList("ammarim073@gmail.com");

        // Créez le service de réduction
        DiscountService discountService = new DiscountService();

        // Ajoutez le listener pour les notifications par e-mail
        discountService.addListener(new EmailNotificationListener(userEmails));

        // Exemple : Créez une réduction
        Discount discount = new Discount(8, 50.0f, new java.util.Date(), new java.util.Date());
        try {
            discountService.insert(discount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
