package tn.esprit.entities;

import java.sql.Timestamp;

public class Event {
     int id;
    private int organizer_id;
    private String event_title;
    private String description;
    private Timestamp start;
    private Timestamp end;
    private String place;

    //constru with Id
    public Event(int id, int organizer_id, String event_title,
                 String description, Timestamp start,
                 Timestamp end, String place) {
        this.id = id;
        this.organizer_id = organizer_id;
        this.event_title = event_title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.place = place;
    }
    //without id
    public Event(int organizer_id, String event_title,
                 String description, Timestamp start,
                 Timestamp end, String place) {
        this.organizer_id = organizer_id;
        this.event_title = event_title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.place = place;
    }
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", organizer_id=" + organizer_id +
                ", event_title='" + event_title + '\'' +
                ", description='" + description + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", place='" + place + '\'' +
                '}';
    }

    // Getters and Setters
    public int getId() {
        return id;
    }



    public int getIdOrganizer() {
        return organizer_id;
    }

    public void setIdOrganizerr(int organizer_id) {
        this.organizer_id = organizer_id;
    }

    public String getEventTitle() {
        return event_title;
    }

    public void setEventTitle(String event_title) {
        this.event_title = event_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
