package tn.esprit.entities;

public class SousCommentaire {
    private int id;
    private int commentaireId;
    private int utilisateurId;
    private String contenu;
    private String dateCreation;

    public SousCommentaire() {}

    public SousCommentaire(int commentaireId, int utilisateurId, String contenu, String dateCreation) {
        this.commentaireId = commentaireId;
        this.utilisateurId = utilisateurId;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
    }
    
    //sans id
    public SousCommentaire(int commentaireId, int utilisateurId, String contenu) {
        this.commentaireId = commentaireId;
        this.utilisateurId = utilisateurId;
        this.contenu = contenu;

    }

    public SousCommentaire(int id, String contenu, int utilisateurId, int commentaireId) {
        this.id = id;
        this.contenu = contenu;
        this.utilisateurId = utilisateurId;
        //this.dateCreation = dateCreation;
        this.commentaireId = commentaireId;
    }



    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommentaireId() {
        return commentaireId;
    }

    public void setCommentaireId(int commentaireId) {
        this.commentaireId = commentaireId;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "SousCommentaire{" +
                "id=" + id +
                ", commentaireId=" + commentaireId +
                ", utilisateurId=" + utilisateurId +
                ", contenu='" + contenu + '\'' +
                ", dateCreation='" + dateCreation + '\'' +
                '}';
    }
}

