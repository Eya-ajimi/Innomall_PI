package tn.esprit.entites;

public class Reclamation {
    private int id;
    private String description;
    private String commentaire;
    private String nomshop;
    private String statut;

    public Reclamation(int id, String description, String commentaire, String nomshop, String statut) {
        this.id = id;
        this.description = description;
        this.commentaire = commentaire;
        this.nomshop = nomshop;
        this.statut = statut;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getNomshop() {
        return nomshop;
    }

    public void setNomshop(String nomshop) {
        this.nomshop = nomshop;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
    @Override
    public String toString() {
        return  description ;
    }
}