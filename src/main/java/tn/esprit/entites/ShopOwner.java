package tn.esprit.entites;

import java.util.List;

public class ShopOwner extends User {
    private String nom;
    private String description;
    private int numeroTicket;
    private List<Produit> produits;

    public ShopOwner(String email, String password, String nom, String description) {
        super(email, password, Role.shopOwner);
        this.nom = nom;
        this.description = description;
    }
    public ShopOwner(int id,String email, String password, String nom, String description) {
        super(id,email, password, Role.shopOwner);
        this.nom = nom;
        this.description = description;
    }

    public ShopOwner(int id,int numeroTicket) {
        super(id);
        this.numeroTicket = numeroTicket;
    }

    public ShopOwner() {

    }
    public int getNumeroTicket() {
        return numeroTicket;
    }

    public void setNumeroTicket(int numeroTicket) {
        this.numeroTicket = numeroTicket;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Getters et setters
    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    @Override
    public String toString() {
        return "ShopOwner{" +
                "description='" + description + '\'' +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ",role= " + role+
                '}';
    }
}


