package tn.esprit.entities;

import java.sql.Timestamp;

public class LikeProduit {
    private int id;
    private int utilisateurId;
    private int produitId;
    private Timestamp dateLike;

    // Constructeur
    public LikeProduit(int utilisateurId, int produitId) {
        this.utilisateurId = utilisateurId;
        this.produitId = produitId;
        this.dateLike = new Timestamp(System.currentTimeMillis()); // Date actuelle
    }

    public LikeProduit(int id,int utilisateurId, int produitId, Timestamp dateLike) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.produitId = produitId;
        this.dateLike = dateLike; // Date actuelle
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public int getProduitId() { return produitId; }
    public void setProduitId(int produitId) { this.produitId = produitId; }

    public Timestamp getDateLike() { return dateLike; }
    public void setDateLike(Timestamp dateLike) { this.dateLike = dateLike; }
}

