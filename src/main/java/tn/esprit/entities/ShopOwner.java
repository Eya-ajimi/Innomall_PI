package tn.esprit.entities;

public class ShopOwner {
    private int id;
    private int user_id;
    private String shop_name;
    private String description;
    private int category_id;

    // Constructor
    public ShopOwner(int id, int user_id, String shop_name, String description, int category_id) {
        this.id = id;
        this.user_id = user_id;
        this.shop_name = shop_name;
        this.description = description;
        this.category_id = category_id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getShopName() {
        return shop_name;
    }

    public void setShopName(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }
}
