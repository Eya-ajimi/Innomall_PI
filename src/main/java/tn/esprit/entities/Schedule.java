package tn.esprit.entities;

import tn.esprit.entities.enums.Days;
import java.sql.Time;

public class Schedule {
    private int id;
    private Days day;
    private Time opening_time;
    private Time closing_time;
    private int shopOwnerId;

    // Constructor
    public Schedule(int id, Days day, Time opening_time, Time closing_time, int shopOwnerId) {
        this.id = id;
        this.day = day;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.shopOwnerId = shopOwnerId;
    }
    // Constructor without
    public Schedule(Days day, Time opening_time, Time closing_time, int shopOwnerId) {

        this.day = day;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.shopOwnerId = shopOwnerId;
    }


    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", day=" + day +
                ", opening_time=" + opening_time +
                ", closing_time=" + closing_time +
                ", shopOwnerId=" + shopOwnerId +
                '}';
    }
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Days getDay() {
        return day;
    }

    public void setDay(Days day) {
        this.day = day;
    }

    public Time getOpeningTime() {
        return opening_time;
    }

    public void setOpeningTime(Time opening_time) {
        this.opening_time = opening_time;
    }

    public Time getClosingTime() {
        return closing_time;
    }

    public void setClosingTime(Time closing_time) {
        this.closing_time = closing_time;
    }

    public int getShopOwnerId() {
        return shopOwnerId;
    }

    public void setShopOwnerId(int shopOwnerId) {
        this.shopOwnerId = shopOwnerId;
    }

    public String getDayOfWeek() {
        return day.name();
    }
}