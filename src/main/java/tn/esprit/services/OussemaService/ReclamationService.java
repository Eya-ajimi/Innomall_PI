package tn.esprit.services.OussemaService;

import tn.esprit.entities.Reclamation;
import tn.esprit.entities.Utilisateur; // Assuming you have a User class
import tn.esprit.utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ReclamationService {
    private Connection connection;

    public ReclamationService() {
        connection = DataBase.getInstance().getCnx();
    }

    /**
     * Adds a new reclamation to the database with the current user's information.
     *
     * @param reclamation The reclamation to add.
     * @param currentUser The current logged-in user.
     * @throws SQLException If a database error occurs.
     */
    public void addReclamation(Reclamation reclamation, Utilisateur currentUser) throws SQLException {
        String query = "INSERT INTO reclamation (description, commentaire, nomshop, statut, id_utilisateur) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, reclamation.getDescription());
        pst.setString(2, reclamation.getCommentaire());
        pst.setString(3, reclamation.getNomshop());
        pst.setString(4, reclamation.getStatut());
        pst.setInt(5, currentUser.getId()); // Add the current user's ID
        pst.executeUpdate();
    }

    /**
     * Updates an existing reclamation in the database.
     *
     * @param reclamation The reclamation to update.
     * @throws SQLException If a database error occurs.
     */
    public void updateReclamation(Reclamation reclamation) throws SQLException {
        // Requête pour mettre à jour la réclamation
        String updateQuery = "UPDATE reclamation SET commentaire = ?, statut = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(updateQuery)) {
            pst.setString(1, reclamation.getCommentaire());
            pst.setString(2, reclamation.getStatut());
            pst.setInt(3, reclamation.getId());
            pst.executeUpdate();
        }

        // Récupérer l'email de l'utilisateur associé à la réclamation
        String selectEmailQuery = "SELECT u.email FROM utilisateur u JOIN reclamation r ON u.id = r.id_utilisateur WHERE r.id = ?";
        try (PreparedStatement psEmail = connection.prepareStatement(selectEmailQuery)) {
            psEmail.setInt(1, reclamation.getId());
            try (ResultSet resultSet = psEmail.executeQuery()) {
                if (resultSet.next()) {
                    String email = resultSet.getString("email");
                    System.out.println(email);

                    // Envoyer un email à l'utilisateur
                    String subject = "Votre réclamation a été traitée";
                    String message = "Votre réclamation a été traitée. Commentaire : " + reclamation.getCommentaire();

                    // Appeler la méthode sendEmail
                    sendEmail(email, subject, message);
                } else {
                    throw new SQLException("Aucun utilisateur trouvé avec cet Id");
                }
            }
        }
    }

    private void sendEmail(String email, String subject, String message) {
        // Paramètres SMTP pour Gmail
        String smtpHost = "smtp.gmail.com";
        int smtpPort = 587;
        String smtpUsername = "Innomall.esprit@gmail.com"; // Remplacez par votre email Gmail
        String smtpPassword = "wlbk qhlt gack trkr"; // Utilisez une variable d'environnement pour le mot de passe

        try {
            // Configuration des propriétés SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            // Création de la session avec authentification
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });

            // Création du message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(smtpUsername));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            msg.setSubject(subject);
            msg.setText(message);

            // Envoi du message
            Transport.send(msg);

            System.out.println("Email envoyé à " + email);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all reclamations from the database.
     *
     * @return A list of reclamations.
     * @throws SQLException If a database error occurs.
     */
    public List<Reclamation> getAllReclamations() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Reclamation reclamation = new Reclamation(
                    rs.getInt("id"),
                    rs.getString("description"),
                    rs.getString("commentaire"),
                    rs.getString("nomshop"),
                    rs.getString("statut")
            );
            reclamations.add(reclamation);
        }
        return reclamations;
    }
}