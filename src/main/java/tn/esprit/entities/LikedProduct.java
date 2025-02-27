package tn.esprit.entities;
import java.sql.Timestamp;
public class LikedProduct {

    private int id;
    private User user;
    private Product product;
    private Timestamp date;
    public LikedProduct() {}

    public LikedProduct(User user, Product product, Timestamp date) {
        this.user = user;
        this.product = product;
        this.date = date;
    }

    public LikedProduct(int id, int userId, int productId, Timestamp date) {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}