package tn.esprit.entities;
import java.util.Date;

public class Discount {
    private int id;
    private int shop_id;
    private float discount_percentage;
    private Date start_date;
    private Date end_date;


    public Discount() {}
    // Constructor
    public Discount(int id, int shop_id, float discount_percentage, Date start_date, Date end_date) {
        this.id = id;
        this.shop_id = shop_id;
        this.discount_percentage = discount_percentage;
        this.start_date = start_date;
        this.end_date = end_date;
    }
    // Constructor without id
    public Discount( int shop_id, float discount_percentage, Date start_date, Date end_date) {

        this.shop_id = shop_id;
        this.discount_percentage = discount_percentage;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    //fazet toString
    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", shop_id=" + shop_id +
                ", discount_percentage=" + discount_percentage +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getShopId() {
        return shop_id;
    }

    public void setShopId(int shop_id) {
        this.shop_id = shop_id;
    }

    public float getDiscountPercentage() {
        return discount_percentage;
    }

    public void setDiscountPercentage(float discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public Date getStartDate() {
        return start_date;
    }

    public void setStartDate(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEndDate() {
        return end_date;
    }

    public void setEndDate(Date end_date) {
        this.end_date = end_date;
    }
}
