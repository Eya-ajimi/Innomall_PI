package tn.esprit.entities;
import tn.esprit.enums.StatutPlace;

public class PlaceParking {



    private int id;
    private String zone;
    private StatutPlace statut;

    // Constructor without ID (auto-generated)
    public PlaceParking(String zone, StatutPlace statut) {
        this.zone = zone;
        this.statut = statut;
    }

    // Constructor with ID (for manual creation)
    public PlaceParking(int id, String zone, StatutPlace statut) {
        this.id = id;
        this.zone = zone;
        this.statut = statut;
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

    @Override
    public String toString() {
        return "PlaceParking{" +
                "id=" + id +
                ", zone='" + zone + '\'' +
                ", statut=" + statut +
                '}';
    }
}
