package tn.esprit.events;

public class DiscountEvent {
    private int shopId;
    private float discountPercentage;
    private java.util.Date startDate;
    private java.util.Date endDate;

    public DiscountEvent(int shopId, float discountPercentage, java.util.Date startDate, java.util.Date endDate) {
        this.shopId = shopId;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getShopId() {
        return shopId;
    }

    public float getDiscountPercentage() {
        return discountPercentage;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }
}