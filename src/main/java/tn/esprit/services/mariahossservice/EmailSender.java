package tn.esprit.services.mariahossservice;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailSender {
        public static void sendEmail(Gmail service, String toEmail, String subject, String body, boolean isHtml) throws MessagingException, IOException {
            // Créez un e-mail
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress("ammarim073@gmail.com")); // list of clients e-mail
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
            email.setSubject(subject);

            // Définir le type de contenu
            if (isHtml) {
                email.setContent(body, "text/html; charset=utf-8");
            } else {
                email.setText(body);
            }

            // Encodez l'e-mail en base64
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

            // Créez un message Gmail
            Message message = new Message();
            message.setRaw(encodedEmail);

            // Envoyez l'e-mail
            service.users().messages().send("me", message).execute();
        }

}