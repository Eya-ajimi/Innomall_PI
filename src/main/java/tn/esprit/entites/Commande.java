package tn.esprit.entites;

import java.util.List;

public class Commande {
    private int id;
    private int idClient;
    private String dateCommande;
    private StatutCommande statut;
    private double total;

    public Commande(int id, int idClient, String dateCommande, double total, StatutCommande statut) {
        this.idClient = idClient;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.total = total;
        this.id = id;

    }

    public Commande(){

    }

    public Commande( int idClient, String dateCommande, StatutCommande statut) {
        this.idClient = idClient;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.total = 0;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public void setDateCommande(String dateCommande) {
        this.dateCommande = dateCommande;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public int getIdClient() {
        return idClient;
    }

    public String getDateCommande() {
        return dateCommande;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", idClient=" + idClient +
                ", dateCommande='" + dateCommande + '\'' +
                ", statut=" + statut +
                ", total=" + total +
                '}';
    }
}
