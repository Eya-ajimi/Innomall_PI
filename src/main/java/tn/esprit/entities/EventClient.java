package tn.esprit.entities;


public class EventClient {

    private int idClient;
    private int idEvent;
    private String date;

    // Constructors, getters, and setters
    public EventClient() {}

    public EventClient(int idClient, int idEvent, String date) {
        this.idClient = idClient;
        this.idEvent = idEvent;
        this.date = date;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}