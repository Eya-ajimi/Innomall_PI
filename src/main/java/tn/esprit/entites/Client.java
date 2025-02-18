package tn.esprit.entites;

public class Client {
    private int id;
    private String nom;

    // Constructors, getters, and setters
    public Client() {}

    public Client(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

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
}