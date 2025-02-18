package tn.esprit.entites;

public class Event {
    private int id;
    private int idOrganisateur;
    private String nomOrganisateur;
    private String description;
    private String dateDebut;
    private String dateFin;
    private String emplacement;

    // Constructors
    public Event() {}

    public Event(int id, int idOrganisateur, String nomOrganisateur, String description, String dateDebut, String dateFin, String emplacement) {
        this.id = id;
        this.idOrganisateur = idOrganisateur;
        this.nomOrganisateur = nomOrganisateur;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.emplacement = emplacement;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOrganisateur() {
        return idOrganisateur;
    }

    public void setIdOrganisateur(int idOrganisateur) {
        this.idOrganisateur = idOrganisateur;
    }

    public String getNomOrganisateur() {
        return nomOrganisateur;
    }

    public void setNomOrganisateur(String nomOrganisateur) {
        this.nomOrganisateur = nomOrganisateur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
}