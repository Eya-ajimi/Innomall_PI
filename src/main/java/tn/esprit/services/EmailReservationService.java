package tn.esprit.services;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailReservationService {
    private static final String FROM_EMAIL = "innomall.esprit@gmail.com"; // Your email address
    private static final String EMAIL_PASSWORD = "wlbk qhlt gack trkr\n"; // Your email password
    private static final String SMTP_HOST = "smtp.gmail.com"; // SMTP host (e.g., smtp.gmail.com for Gmail)
    private static final int SMTP_PORT = 587; // SMTP port (587 for TLS)

    /**
     * Sends a confirmation email to the driver after a reservation is made.
     *
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param body    The HTML content of the email.
     */
    public void sendReservationConfirmationEmail(String to, String subject, String body) {
        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
            }
        });

        try {
            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            // Send the email
            Transport.send(message);
            System.out.println("E-mail de confirmation envoyé avec succès à : " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }
}