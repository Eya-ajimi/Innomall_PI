package tn.esprit.services;

import tn.esprit.entites.Event;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService implements CRUD<Event> {
    private Connection connection;

    public EventService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Event event) {
        String query = "INSERT INTO event (idOrganisateur, nomOrganisateur, description, dateDebut, dateFin, emplacement) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, event.getIdOrganisateur());
            pst.setString(2, event.getNomOrganisateur());
            pst.setString(3, event.getDescription());
            pst.setString(4, event.getDateDebut());
            pst.setString(5, event.getDateFin());
            pst.setString(6, event.getEmplacement());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Event event) {
        String query = "UPDATE event SET idOrganisateur=?, nomOrganisateur=?, description=?, dateDebut=?, dateFin=?, emplacement=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, event.getIdOrganisateur());
            pst.setString(2, event.getNomOrganisateur());
            pst.setString(3, event.getDescription());
            pst.setString(4, event.getDateDebut());
            pst.setString(5, event.getDateFin());
            pst.setString(6, event.getEmplacement());
            pst.setInt(7, event.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM event WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> getAll() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getInt("idOrganisateur"),
                        rs.getString("nomOrganisateur"),
                        rs.getString("description"),
                        rs.getString("dateDebut"),
                        rs.getString("dateFin"),
                        rs.getString("emplacement")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event getById(int id) {
        Event event = null;
        String query = "SELECT * FROM event WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                event = new Event(
                        rs.getInt("id"),
                        rs.getInt("idOrganisateur"),
                        rs.getString("nomOrganisateur"),
                        rs.getString("description"),
                        rs.getString("dateDebut"),
                        rs.getString("dateFin"),
                        rs.getString("emplacement")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    // New method to get events by organizer ID
    public List<Event> getEventsByOrganizer(int idOrganisateur) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE idOrganisateur = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idOrganisateur);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getInt("idOrganisateur"),
                        rs.getString("nomOrganisateur"),
                        rs.getString("description"),
                        rs.getString("dateDebut"),
                        rs.getString("dateFin"),
                        rs.getString("emplacement")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Debug: Print the events fetched
        System.out.println("Events fetched for organizer " + idOrganisateur + ": " + events);
        return events;
    }
}