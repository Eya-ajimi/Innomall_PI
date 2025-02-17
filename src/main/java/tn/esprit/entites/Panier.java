package tn.esprit.entites;

public class Panier {
    private int idCommande;
    private int idProduit;
    private int quantite;

    /*Other attributes for jointure */

    private String nomProduit;
    private double prix;
    private String description;

    public Panier(int idCommande, int idProduit, int quantite) {
        this.idCommande = idCommande;
        this.idProduit = idProduit;
        this.quantite = quantite;
    }

    public Panier(int idCommande, int idProduit, int quantite, String nomProduit, double prix, String description) {
        this.idCommande = idCommande;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.nomProduit = nomProduit;
        this.prix = prix;
        this.description = description;

    }


    @Override
    public String toString() {
        return "Panier{" +
                "idCommande=" + idCommande +
                ", idProduit=" + idProduit +
                ", quantite=" + quantite +
                ", nomProduit='" + nomProduit + '\'' +
                ", prix=" + prix +
                ", description='" + description + '\'' +
                '}';
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
