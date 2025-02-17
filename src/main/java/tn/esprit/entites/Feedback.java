package tn.esprit.entites;
import java.sql.Timestamp;

public class Feedback {
    private int id;
    private int utilisateurId;  // L'utilisateur qui donne le feedback
    private int shopId;         // Le shop noté
    private int rating;         // Note sur 5
    private Timestamp dateFeedback;

    // 🔹 Constructeur vide
    public Feedback() {}

    // 🔹 Constructeur avec paramètres
    public Feedback(int utilisateurId, int shopId, int rating) {
        this.utilisateurId = utilisateurId;
        this.shopId = shopId;
        this.rating = rating;
    }

    // ✅ GETTERS et SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }

    public int getRating() { return rating; }
    public void setRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        } else {
            throw new IllegalArgumentException("La note doit être entre 1 et 5.");
        }
    }

    public Timestamp getDateFeedback() { return dateFeedback; }
    public void setDateFeedback(Timestamp dateFeedback) { this.dateFeedback = dateFeedback; }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", utilisateurId=" + utilisateurId +
                ", shopId=" + shopId +
                ", rating=" + rating +
                ", dateFeedback=" + dateFeedback +
                '}';
    }
}
