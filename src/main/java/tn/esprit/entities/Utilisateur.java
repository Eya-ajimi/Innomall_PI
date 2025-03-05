package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private static String motDePasse;
    private int points;
    private int nombreDeGain;
    private String adresse;
    private String telephone;
    private LocalDateTime dateInscription;
    private String statut;
    private Role role;
    private int idCategorie; // Nouvel attribut pour l'ID de la catégorie
    private String nomCategorie; // Nouvel attribut pour le nom de la catégorie
    private String description;
    private static byte[] profilePicture;
    private double balance;
    private int numeroTicket;
    // Constructeur par défaut
    public Utilisateur() {
        this.points = 0; // Valeur par défaut
        this.nombreDeGain = 0; // Valeur par défaut
        this.dateInscription = LocalDateTime.now(); // Initialisation à l'heure actuelle
    }

    public Utilisateur (int id , int numeroTicket){
        this.id = id;
        this.numeroTicket = numeroTicket;
    }
    // Constructeur avec tous les attributs
    public Utilisateur(int id, String nom, String prenom, String email, String motDePasse, int points, int nombreDeGain, String adresse, String telephone, LocalDateTime dateInscription, String statut, Role role, int idCategorie, String nomCategorie, String description, byte[] profilePicture) {
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
        this.idCategorie = idCategorie;
        this.nomCategorie = nomCategorie;
        this.description = description;
        this.profilePicture = profilePicture;
    }

    // Constructeur utilisé lors de l'inscription
    public Utilisateur(String nom, String prenom, String email, String motDePasse, String adresse, String telephone, String statut, Role role, String description) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresse = adresse;
        this.telephone = telephone;
        this.statut = statut;
        this.role = role;
        this.description = description;
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

    public static String getMotDePasse() {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public static byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    // Getters et Setters pour idCategorie et nomCategorie
    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getNumeroTicket() {
        return numeroTicket;
    }

    public void setNumeroTicket(int numeroTicket) {
        this.numeroTicket = numeroTicket;
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
                ", idCategorie=" + idCategorie +
                ", nomCategorie='" + nomCategorie + '\'' +
                '}';
    }

    // equals et hashCode pour comparer des objets User
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur user = (Utilisateur) o;
        return id == user.id && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}