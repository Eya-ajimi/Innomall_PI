package tn.esprit.entities;

public class Commentaire {
    private int id;
    private int postId;
    private int utilisateurId;
    private String contenu;
    private String dateCreation;

    public Commentaire() {}

    public Commentaire(int postId, int utilisateurId, String contenu, String dateCreation) {
        this.postId = postId;
        this.utilisateurId = utilisateurId;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
    }
    //sans id

    public Commentaire(int postId, int utilisateurId, String contenu) {
        this.postId = postId;
        this.utilisateurId = utilisateurId;
        this.contenu = contenu;

    }




    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public int getPosteId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
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
        return "Commentaire{" +
                "id=" + id +
                ", postId=" + postId +
                ", utilisateurId=" + utilisateurId +
                ", contenu='" + contenu + '\'' +
                ", dateCreation='" + dateCreation + '\'' +
                '}';
    }


}
