package tn.esprit.entites;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private int points;
    private int nombreDeGain;
    private String adresse;
    private String telephone;
    private LocalDateTime dateInscription;
    private String statut;
    private String role;

    // Constructeur par défaut
    public User() {
        this.points = 0; // Valeur par défaut
        this.nombreDeGain = 0; // Valeur par défaut
        this.dateInscription = LocalDateTime.now(); // Initialisation à l'heure actuelle
    }

    // Constructeur avec tous les attributs
    public User(int id, String nom, String prenom, String email, String motDePasse, int points, int nombreDeGain, String adresse, String telephone, LocalDateTime dateInscription, String statut, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.points = points;
        this.nombreDeGain = nombreDeGain;
        this.adresse = adresse;
        this.telephone = telephone;
        this.dateInscription = dateInscription;
        this.statut = statut;
        this.role = role;
    }

    // Constructeur utilisé lors de l'inscription
    public User(String nom, String prenom, String email, String motDePasse, String adresse, String telephone, String statut, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresse = adresse;
        this.telephone = telephone;
        this.statut = statut;
        this.role = role;
        this.points = 0; // Valeur par défaut
        this.nombreDeGain = 0; // Valeur par défaut
        this.dateInscription = LocalDateTime.now(); // Initialisation à l'heure actuelle
    }

    // Getters et Setters
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

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", points=" + points +
                ", nombreDeGain=" + nombreDeGain +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", dateInscription=" + dateInscription +
                ", statut='" + statut + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    // equals et hashCode pour comparer des objets User
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
