package tn.esprit.entities;

import tn.esprit.entities.enums.Role;

import java.util.Date;

public class User {

    private int id;
    private String email;
    private String password;
    private Date subscription_date;
    private Role role;

    // Constructor
    public User(int id, String email, String password, Date subscription_date, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.subscription_date = subscription_date;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getSubscriptionDate() {
        return subscription_date;
    }

    public void setSubscriptionDate(Date subscription_date) {
        this.subscription_date = subscription_date;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
