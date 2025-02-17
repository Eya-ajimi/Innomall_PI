package tn.esprit.entities;

import java.sql.Timestamp;

public class Reservation {
    public enum StatutReservation {
        reserved,
        expired,
        canceled,
    }

    private int idReservation;
    private int idUtilisateur; // Changed from idClient
    private int idParking;
    private Timestamp dateReservation;
    private Timestamp dateExpiration;
    private StatutReservation statut;

    // Constructor that accepts the expected parameters
    public Reservation(int idUtilisateur, int idParking, Timestamp dateReservation, Timestamp dateExpiration, StatutReservation statut) {
        this.idUtilisateur = idUtilisateur; // Changed from idClient
        this.idParking = idParking;
        this.dateReservation = dateReservation;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
    }

    // Getters and Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdUtilisateur() { // Changed from getIdClient
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) { // Changed from setIdClient
        this.idUtilisateur = idUtilisateur;
    }

    public int getIdParking() {
        return idParking;
    }

    public void setIdParking(int idParking) {
        this.idParking = idParking;
    }

    public Timestamp getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Timestamp dateReservation) {
        this.dateReservation = dateReservation;
    }

    public Timestamp getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Timestamp dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", idUtilisateur=" + idUtilisateur + // Updated field
                ", idParking=" + idParking +
                ", dateReservation=" + dateReservation +
                ", dateExpiration=" + dateExpiration +
                ", statut=" + statut +
                '}';
    }
}
