package tn.esprit.entites;

public class Produit {
    private int id;
    private int shopId;
    private String nom;
    private String description;
    private int promotionId;
    private int stock;
    private double prix;

    public Produit(int id,int shopId, String description, int promotionId, int stock, double prix, String nom) {
        this.shopId = shopId;
        this.description = description;
        this.promotionId = promotionId;
        this.stock = stock;
        this.prix = prix;
        this.nom = nom;
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getShopId() {
        return shopId;
    }

    public String getDescription() {
        return description;
    }

    public int getPromotionId() {
        return promotionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getStock() {
        return stock;
    }

    public double getPrix() {
        return prix;
    }
}
