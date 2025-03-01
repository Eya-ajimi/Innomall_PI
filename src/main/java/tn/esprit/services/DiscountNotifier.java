package tn.esprit.services;
import com.google.api.services.gmail.Gmail;
import tn.esprit.utils.GmailServiceFactory;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public class DiscountNotifier {

        public static void notifyUsers(List<String> userEmails, String subject, String body) {
            try {
                Gmail service = GmailServiceFactory.getGmailService();
                for (String email : userEmails) {
                    EmailSender.sendEmail(service, email, subject, body);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}