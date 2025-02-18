package tn.esprit.entities;

public class Category {
    private int id;
    private String name;

    // Constructor
    public Category( String name) {
        this.name = name;
    }

    // Getters and Setters
    public int getIdCategory() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
