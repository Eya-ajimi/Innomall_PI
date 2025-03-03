package tn.esprit.services.OussemaService;

import tn.esprit.entities.Event;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventService implements CRUD<Event> {
    private Connection connection;

    public EventService() {
        this.connection = DataBase.getInstance().getCnx();
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
    public List<Event> getEventsByDate(LocalDate date) {
        List<Event> events = new ArrayList<>();
        // Convert LocalDate to DD-MM-YYYY format
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        System.out.println("Searching for events on date: " + formattedDate); // Debugging

        String query = "SELECT * FROM event WHERE STR_TO_DATE(?, '%d-%m-%Y') BETWEEN STR_TO_DATE(dateDebut, '%d-%m-%Y') AND STR_TO_DATE(dateFin, '%d-%m-%Y')";
        System.out.println("Executing query: " + query); // Debugging

        try (Connection connection = DataBase.getInstance().getCnx();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, formattedDate);
            System.out.println("Query parameter: " + formattedDate); // Debugging

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Event event = new Event();
                    event.setId(resultSet.getInt("id"));
                    event.setNomOrganisateur(resultSet.getString("nomOrganisateur"));
                    event.setDescription(resultSet.getString("description"));
                    event.setDateDebut(resultSet.getString("dateDebut"));
                    event.setDateFin(resultSet.getString("dateFin"));
                    event.setEmplacement(resultSet.getString("emplacement"));
                    events.add(event);
                    System.out.println("Found event: " + event.getNomOrganisateur() + " (" + event.getDateDebut() + " to " + event.getDateFin() + ")"); // Debugging
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error: " + e.getMessage()); // Debugging
        }

        if (events.isEmpty()) {
            System.out.println("No events found for the selected date."); // Debugging
        }

        return events;
    }
}