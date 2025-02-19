package tn.esprit.entities;
import tn.esprit.enums.Role;


public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private int points;
    private int nombreDeGain;
    private String adresse;
    private String telephone;
    private String statut;
    private Role role;

    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String motDePasse, int points, int nombreDeGain, String adresse, String telephone, String statut, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.points = points;
        this.nombreDeGain = nombreDeGain;
        this.adresse = adresse;
        this.telephone = telephone;
        this.statut = statut;
        this.role = role;
    }


    public Utilisateur(int id,String nom, String prenom, String email, String motDePasse, int points, int nombreDeGain, String adresse, String telephone, String statut, Role role) {
        this.id=id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.points = points;
        this.nombreDeGain = nombreDeGain;
        this.adresse = adresse;
        this.telephone = telephone;
        this.statut = statut;
        this.role = role;
    }

    public Utilisateur(String nom, String prenom, String email, String motDePasse, String adresse, String telephone, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresse = adresse;
        this.telephone = telephone;
        this.role = role;
    }



    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getNombreDeGain() {
        return nombreDeGain;
    }

    public void setNombreDeGain(int nombreDeGain) {
        this.nombreDeGain = nombreDeGain;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", points=" + points +
                ", nombreDeGain=" + nombreDeGain +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", statut='" + statut + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}