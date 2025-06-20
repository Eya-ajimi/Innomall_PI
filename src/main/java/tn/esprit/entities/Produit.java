package tn.esprit.entities;

public class Produit {
    private int id;
    private int shopId;
    private Integer promotionId;
    private String nom;
    private String description;
    private int stock;
    private double prix;
    private String photoUrl; // New attribute for storing the photo URL

    public Produit() {

    }
    public Produit(int id,int shopId, String description, int promotionId, int stock, double prix, String nom) {
        this.shopId = shopId;
        this.description = description;
        this.promotionId = promotionId;
        this.stock = stock;
        this.prix = prix;
        this.nom = nom;
        this.id = id;
    }
    // Constructor with product id for updating
    public Produit(int id, int shopId, int promotionId, String nom, String description, int stock, double prix, String photoUrl) {
        this.id = id;
        this.shopId = shopId;
        this.promotionId = promotionId;
        this.nom = nom;
        this.description = description;
        this.stock = stock;
        this.prix = prix;
        this.photoUrl = photoUrl;
    }




    // Constructor without product id
//    public Produit(int shop_id, Integer discount_id, String title, String description, int stock, float price, String photoUrl) {
//        this.shop_id = shop_id;
//        this.discount_id = discount_id;
//        this.title = title;
//        this.description = description;
//        this.stock = stock;
//        this.price = price;
//        this.photoUrl = photoUrl;
//    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", shopId=" + shopId +
                ", promotionId=" + promotionId +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", stock=" + stock +
                ", prix=" + prix +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}