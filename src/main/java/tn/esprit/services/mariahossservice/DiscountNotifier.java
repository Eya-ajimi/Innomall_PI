package tn.esprit.services.mariahossservice;
import com.google.api.services.gmail.Gmail;
import tn.esprit.utils.GmailServiceFactory;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public class DiscountNotifier {

    public static void notifyUsers(List<String> userEmails, String subject, String body, boolean isHtml) {
        try {
            Gmail service = GmailServiceFactory.getGmailService();
            for (String email : userEmails) {
                EmailSender.sendEmail(service, email, subject, body, isHtml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
