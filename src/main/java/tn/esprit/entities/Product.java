package tn.esprit.entities;

public class Product {
    private int id;
    private int shop_id;
    private Integer discount_id;
    private String description;
    private int stock;
    private float price;


   public Product(){};
   //constru with the product id for updating
   public Product(int id, int shop_id, Integer discount_id, String description, int stock, float price) {
       this.id = id;
       this.shop_id = shop_id;
       this.discount_id = discount_id;
       this.description = description;
       this.stock = stock;
       this.price = price;
   }

    //without the product id
    public Product(int shop_id, Integer discount_id, String description, int stock, float price) {
        this.shop_id = shop_id;
        // Check if discount_id is null or invalid, set to null (or any default value like 0)
        this.discount_id = discount_id;
        this.description = description;
        this.stock = stock;
        this.price = price;
    }


    @Override
    public String toString() {
        return "Product{id=" + id + ", shop_id=" + shop_id + ", discount_id=" + discount_id +
                ", description='" + description + "', stock=" + stock + ", price=" + price + "}";
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
}
