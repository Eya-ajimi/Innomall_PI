package tn.esprit.entites;

public class Promotion {
    private int id;
    private double promoPourcentage;
    private String dateDebut;
    private String dateFin;

    public Promotion(int id, double promoPourcentage, String dateDebut, String dateFin) {
        this.id = id;
        this.promoPourcentage = promoPourcentage;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public double getPromoPourcentage() {
        return promoPourcentage;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }
}
