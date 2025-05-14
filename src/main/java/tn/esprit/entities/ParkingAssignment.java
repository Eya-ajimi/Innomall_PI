package tn.esprit.entities;

import java.sql.Timestamp;

public class ParkingAssignment {
    private int id;
    private int placeParkingId;
    private String phoneNumber;
    private Timestamp scannedAt;

    // Constructors
    public ParkingAssignment() {}

    public ParkingAssignment(int id, int placeParkingId, String phoneNumber, Timestamp scannedAt) {
        this.id = id;
        this.placeParkingId = placeParkingId;
        this.phoneNumber = phoneNumber;
        this.scannedAt = scannedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaceParkingId() {
        return placeParkingId;
    }

    public void setPlaceParkingId(int placeParkingId) {
        this.placeParkingId = placeParkingId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Timestamp getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(Timestamp scannedAt) {
        this.scannedAt = scannedAt;
    }

    @Override
    public String toString() {
        return "ParkingAssignment{" +
                "id=" + id +
                ", placeParkingId=" + placeParkingId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", scannedAt=" + scannedAt +
                '}';
    }
}