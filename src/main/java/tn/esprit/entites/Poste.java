package tn.esprit.entites;

public class Poste {
    private int id;
    private int utilisateurId;
    private String contenu;
    private String dateCreation;

    public Poste() {}

    public Poste(int utilisateurId, String contenu, String dateCreation) {
        this.utilisateurId = utilisateurId;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
    }

    public Poste( int utilisateurId,String contenu) {
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
        return "Poste{" +
                "id=" + id +
                ", utilisateurId=" + utilisateurId +
                ", contenu='" + contenu + '\'' +
                ", dateCreation='" + dateCreation + '\'' +
                '}';
    }


}

