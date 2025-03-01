package tn.esprit.entities;
import java.sql.Timestamp;
public class LikedProduct {

    private int id;
    private Utilisateur user;
    private Produit produit;
    private Timestamp date;
    public LikedProduct() {}

    public LikedProduct(Utilisateur user, Produit produit, Timestamp date) {
        this.user = user;
        this.produit = produit;
        this.date = date;
    }

    public LikedProduct(int id, int userId, int productId, Timestamp date) {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return user;
    }

    public void setUtilisateur(Utilisateur user) {
        this.user = user;
    }

    public Produit getProduct() {
        return produit;
    }

    public void setProduct(Produit produit) {
        this.produit = produit;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}