package tn.esprit.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javax.mail.Session;

public class EmailService {

    public static void sendPasswordResetEmail(String recipientEmail, int resetToken) {
        final String username = "houssemjribi111@gmail.com"; // Remplacez par votre e-mail Gmail
        final String password = "uwjn zigp simo kgbs"; // Remplacez par votre mot de passe d'application

        // Configuration SMTP pour Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // Authentification requise
        props.put("mail.smtp.starttls.enable", "true"); // Activation de STARTTLS
        props.put("mail.smtp.host", "smtp.gmail.com"); // Serveur SMTP de Gmail
        props.put("mail.smtp.port", "587"); // Port SMTP pour TLS
        props.put("mail.debug", "true"); // Activer les logs SMTP pour le débogage

        // Création de la session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Expéditeur
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail)); // Destinataire
            message.setSubject("Réinitialisation de mot de passe"); // Sujet de l'e-mail
            message.setText("Voici votre code  : " + resetToken); // Corps du message

            // Envoi du message
            Transport.send(message);
            System.out.println("E-mail envoyé avec succès à " + recipientEmail);
        } catch (MessagingException e) {
            // Gestion des erreurs
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            e.printStackTrace(); // Affiche la stack trace complète
            throw new RuntimeException("Échec de l'envoi de l'e-mail : " + e.getMessage());
        }
    }
}