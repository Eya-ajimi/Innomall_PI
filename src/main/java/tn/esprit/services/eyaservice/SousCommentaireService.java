package tn.esprit.services.eyaservice;

import tn.esprit.entities.SousCommentaire;
import tn.esprit.entities.Utilisateur;
import tn.esprit.utils.DataBase;
import tn.esprit.utils.EmailService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SousCommentaireService implements CRUD<SousCommentaire> {

    private Connection cnx = DataBase.getInstance().getCnx();
    private Statement st;
    private PreparedStatement ps;

    @Override
    public void create(Utilisateur user) throws SQLException {

    }

    @Override
    public int insert(SousCommentaire sousCommentaire) throws SQLException {
        String req = "INSERT INTO sous_commentaires (contenu, commentaire_id, utilisateur_id) VALUES (?, ?, ?)";
        ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, sousCommentaire.getContenu());
        ps.setInt(2, sousCommentaire.getCommentaireId());
        ps.setInt(3, sousCommentaire.getUtilisateurId());

        int rowsAffected = ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            sousCommentaire.setId(generatedKeys.getInt(1)); // Met à jour l'ID de l'objet sousCommentaire
        } else {
            throw new SQLException("Erreur: Impossible de récupérer l'ID du sous-commentaire inséré.");
        }

        // Vérifier si l'auteur du post est celui qui répond au commentaire
        String getPostAuthorQuery = "SELECT p.utilisateur_id AS auteur_post, c.utilisateur_id AS auteur_commentaire " +
                "FROM commentaires c " +
                "JOIN postes p ON c.post_id = p.id " +
                "WHERE c.id = ?";
        PreparedStatement psCheck = cnx.prepareStatement(getPostAuthorQuery);
        psCheck.setInt(1, sousCommentaire.getCommentaireId());
        ResultSet rs = psCheck.executeQuery();

        if (rs.next()) {
            int auteurPost = rs.getInt("auteur_post");
            int auteurCommentaire = rs.getInt("auteur_commentaire");

            // Si l'auteur du post répond au commentaire, donner 10 points à l’auteur du commentaire
            if (auteurPost == sousCommentaire.getUtilisateurId()) {
                // Récupérer les points actuels et l'email de l'utilisateur
                String getUserInfoQuery = "SELECT points, email FROM utilisateur WHERE id = ?";
                PreparedStatement psUserInfo = cnx.prepareStatement(getUserInfoQuery);
                psUserInfo.setInt(1, auteurCommentaire);
                ResultSet rsUserInfo = psUserInfo.executeQuery();

                if (rsUserInfo.next()) {
                    int currentPoints = rsUserInfo.getInt("points");
                    String email = rsUserInfo.getString("email");

                    // Mettre à jour les points de l'utilisateur
                    String updatePointsQuery = "UPDATE utilisateur SET points = points + 10 WHERE id = ?";
                    PreparedStatement psUpdate = cnx.prepareStatement(updatePointsQuery);
                    psUpdate.setInt(1, auteurCommentaire);
                    psUpdate.executeUpdate();
                    System.out.println("10 points ajoutés à l'utilisateur ID " + auteurCommentaire);

                    // Envoyer un email à l'utilisateur avec les nouveaux points
                    int newPoints = currentPoints + 10;

                    String smtpHost = "smtp.gmail.com"; // Hôte SMTP de Gmail
                    int smtpPort = 587; // Port SMTP de Gmail
                    String smtpUsername = "Innomall.esprit@gmail.com"; // Votre email Gmail
                    String smtpPassword = "wlbk qhlt gack trkr"; // Votre mot de passe Gmail

                    sendEmail(email, newPoints, smtpHost, smtpPort, smtpUsername, smtpPassword);
                }
            }
        }

        return rowsAffected;
    }

    private void sendEmail(String email, int newPoints, String smtpHost, int smtpPort, String smtpUsername, String smtpPassword) {
        // Sujet et contenu de l'email
        String subject = "Vos points ont été mis à jour";
        String message = "Félicitations ! Vous avez maintenant " + newPoints + " points.";

        try {
            // Configuration des propriétés SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost); // Hôte SMTP (ex: smtp.gmail.com)
            props.put("mail.smtp.port", smtpPort); // Port SMTP (ex: 587 pour Gmail)
            props.put("mail.smtp.auth", "true"); // Authentification requise
            props.put("mail.smtp.starttls.enable", "true"); // Activation de TLS

            // Création de la session avec authentification
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword); // Email et mot de passe SMTP
                }
            });

            // Création du message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(smtpUsername)); // L'email de l'expéditeur
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email)); // L'email du destinataire
            msg.setSubject(subject); // Sujet de l'email
            msg.setText(message); // Contenu de l'email

            // Envoi du message
            Transport.send(msg);

            System.out.println("Email envoyé à " + email);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        }
    }




    @Override
    public int update(SousCommentaire sousCommentaire) throws SQLException {
        // Check if the sous-commentaire exists and the user is the owner
        String checkSousCommentaire = "SELECT id FROM sous_commentaires WHERE id = ? AND utilisateur_id = ?";
        ps = cnx.prepareStatement(checkSousCommentaire);
        ps.setInt(1, sousCommentaire.getId());
        ps.setInt(2, sousCommentaire.getUtilisateurId());

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Vous n'êtes pas autorisé à modifier ce sous-commentaire.");
        }

        // Proceed with the update
        String req = "UPDATE sous_commentaires SET contenu = ? WHERE id = ?";
        ps = cnx.prepareStatement(req);
        ps.setString(1, sousCommentaire.getContenu());
        ps.setInt(2, sousCommentaire.getId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Aucune mise à jour effectuée. Vérifiez l'ID.");
        }

        return rowsAffected;
    }

    @Override
    public int delete(SousCommentaire sousCommentaire) throws SQLException {
        // Check if the sous-commentaire exists and the user is the owner
        String checkSousCommentaire = "SELECT id FROM sous_commentaires WHERE id = ? AND utilisateur_id = ?";
        ps = cnx.prepareStatement(checkSousCommentaire);
        ps.setInt(1, sousCommentaire.getId());
        ps.setInt(2, sousCommentaire.getUtilisateurId());

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Vous n'êtes pas autorisé à supprimer ce sous-commentaire.");
        }

        // Proceed with the deletion
        String req = "DELETE FROM sous_commentaires WHERE id = ?";
        ps = cnx.prepareStatement(req);
        ps.setInt(1, sousCommentaire.getId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Aucune suppression effectuée. Vérifiez l'ID.");
        }

        return rowsAffected;
    }


    // Méthode pour récupérer les sous-commentaires d'un commentaire donné
    public List<SousCommentaire> getSousCommentairesByCommentaireId(int commentaireId) throws SQLException {
        List<SousCommentaire> sousCommentaires = new ArrayList<>();
        String req = "SELECT * FROM sous_commentaires WHERE commentaire_id = ?";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, commentaireId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            SousCommentaire sousCommentaire = new SousCommentaire();
            sousCommentaire.setId(rs.getInt("id"));
            sousCommentaire.setCommentaireId(rs.getInt("commentaire_id"));
            sousCommentaire.setUtilisateurId(rs.getInt("utilisateur_id"));
            sousCommentaire.setContenu(rs.getString("contenu"));
            //sousCommentaire.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());

            sousCommentaires.add(sousCommentaire);
        }

        return sousCommentaires;
    }


    @Override
    public List<SousCommentaire> showAll() throws SQLException {
        List<SousCommentaire> temp = new ArrayList<>();
        String req = "SELECT * FROM sous_commentaires";

        st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            SousCommentaire sousCommentaire = new SousCommentaire();
            sousCommentaire.setId(rs.getInt("id"));
            sousCommentaire.setContenu(rs.getString("contenu"));
            sousCommentaire.setCommentaireId(rs.getInt("commentaire_id"));
            sousCommentaire.setUtilisateurId(rs.getInt("utilisateur_id"));
            sousCommentaire.setDateCreation(rs.getString("date_creation"));
            temp.add(sousCommentaire);
        }

        return temp;
    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public List<Utilisateur> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Utilisateur getOneById() throws SQLException {
        return null;
    }

    @Override
    public Utilisateur getOneById(int id) throws SQLException {
        return null;
    }
    /************show comment by id *********/









}