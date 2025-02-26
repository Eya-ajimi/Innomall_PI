package tn.esprit.entities;

public class Product {
    private int id;
    private int shop_id;
    private Integer discount_id;
    private String title;
    private String description;
    private int stock;
    private float price;
    private String photoUrl; // New attribute for storing the photo URL

    public Product() {}

    // Constructor with product id for updating
    public Product(int id, int shop_id, Integer discount_id, String title, String description, int stock, float price, String photoUrl) {
        this.id = id;
        this.shop_id = shop_id;
        this.discount_id = discount_id;
        this.title = title;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.photoUrl = photoUrl;
    }

    // Constructor without product id
    public Product(int shop_id, Integer discount_id, String title, String description, int stock, float price, String photoUrl) {
        this.shop_id = shop_id;
        this.discount_id = discount_id;
        this.title = title;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", shop_id=" + shop_id + ", discount_id=" + discount_id +
                ", title='" + title + "', description='" + description + "', stock=" + stock +
                ", price=" + price + ", photoUrl='" + photoUrl + "'}";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public Integer getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(Integer discount_id) {
        this.discount_id = discount_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
