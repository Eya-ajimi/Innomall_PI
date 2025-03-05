package tn.esprit.services.mariahossservice;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.mail.internet.MimeMessage.RecipientType;

import java.util.Properties;

public class EmailSender {
    private Gmail gmailService;

    // Constructor to initialize Gmail service
    public EmailSender(Gmail gmailService) {
        this.gmailService = gmailService;
    }

    public Gmail getGmailService() {
        return gmailService;
    }

    public static void sendEmail(Gmail service, String toEmail, String subject, String body, boolean isHtml) throws MessagingException, IOException {
        // Create email
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress("Innomall.esprit@gmail.com")); // Sender's email
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
        email.setSubject(subject);

        // Set content type
        if (isHtml) {
            email.setContent(body, "text/html; charset=utf-8");
        } else {
            email.setText(body);
        }

        // Encode email to base64
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

        // Create Gmail message
        Message message = new Message();
        message.setRaw(encodedEmail);

        // Send email
        service.users().messages().send("me", message).execute();
    }

    public static void sendEmailWithAttachment(Gmail service, String toEmail, String subject, String body, File file) throws MessagingException, IOException {
        // Check if file exists
        if (!file.exists()) {
            throw new IOException("File does not exist: " + file.getAbsolutePath());
        }

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress("ammarim073@gmail.com"));
        email.addRecipient(RecipientType.TO, new InternetAddress(toEmail));
        email.setSubject(subject);

        // Create multipart email
        MimeMultipart multipart = new MimeMultipart();

        // Email body
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);
        multipart.addBodyPart(textPart);

        // Attachment
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(file);
        multipart.addBodyPart(attachmentPart);

        email.setContent(multipart);

        sendGmailMessage(service, email);
    }

    private static void sendGmailMessage(Gmail service, MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        service.users().messages().send("me", message).execute();
    }
}