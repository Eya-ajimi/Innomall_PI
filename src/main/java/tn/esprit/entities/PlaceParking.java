package tn.esprit.entities;

import tn.esprit.enums.StatutPlace;

public class PlaceParking {
    private int id;
    private String zone;
    private StatutPlace statut;
    private String floor; // New field for floor

    // Constructors
    public PlaceParking() {}

    public PlaceParking(int id, String zone, StatutPlace statut, String floor) {
        this.id = id;
        this.zone = zone;
        this.statut = statut;
        this.floor = floor;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public StatutPlace getStatut() {
        return statut;
    }

    public void setStatut(StatutPlace statut) {
        this.statut = statut;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "PlaceParking{" +
                "id=" + id +
                ", zone='" + zone + '\'' +
                ", statut=" + statut +
                ", floor='" + floor + '\'' +
                '}';
    }
}