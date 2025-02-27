package tn.esprit.entities;

public class Feedback {
    private int id;
    private int client_id;
    private int shopowner_id;
    private int rating;

    // Constructor
    public Feedback(int id, int client_id, int shopowner_id, int rate) {
        this.id = id;
        this.client_id = client_id;
        this.shopowner_id = shopowner_id;
        this.rating = rate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }


    public int getClientId() {
        return client_id;
    }

    public void setClientId(int client_id) {
        this.client_id = client_id;
    }

    public int getShopownerId() {
        return shopowner_id;
    }

    public void setShopownerId(int Shopowner_id) {
        this.shopowner_id = Shopowner_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rate) {
        this.rating = rate;
    }
}
