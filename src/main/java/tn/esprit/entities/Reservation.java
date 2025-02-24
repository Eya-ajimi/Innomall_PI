package tn.esprit.entities;

import java.sql.Timestamp;

public class Reservation {
    public enum StatutReservation {
        reserved,
        expired,
        canceled,
    }

    private int idReservation;
    private int idUtilisateur;
    private int idParking;
    private Timestamp dateReservation;
    private Timestamp dateExpiration;
    private StatutReservation statut;
    private String vehicleType;
    private String carWashType;
    private String notes;
    private double price;

    public Reservation(int idUtilisateur, int idParking, Timestamp dateReservation, Timestamp dateExpiration, StatutReservation statut, String vehicleType, String carWashType, String notes, double price) {
        this.idUtilisateur = idUtilisateur;
        this.idParking = idParking;
        this.dateReservation = dateReservation;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
        this.vehicleType = vehicleType;
        this.carWashType = carWashType;
        this.notes = notes;
        this.price = price;
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
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCarWashType() {
        return carWashType;
    }

    public void setCarWashType(String carWashType) {
        this.carWashType = carWashType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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