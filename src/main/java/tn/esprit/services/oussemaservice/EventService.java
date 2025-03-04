package tn.esprit.services.oussemaservice;

import tn.esprit.entities.Event;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.CRUD;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventService implements CRUD<Event> {

    @Override
    public void add(Event event) {
        String query = "INSERT INTO event (idOrganisateur, nomOrganisateur, description, dateDebut, dateFin, emplacement) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataBase.getInstance().getCnx(); // Get a new connection
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, event.getIdOrganisateur());
            pst.setString(2, event.getNomOrganisateur());
            pst.setString(3, event.getDescription());
            pst.setString(4, event.getDateDebut());
            pst.setString(5, event.getDateFin());
            pst.setString(6, event.getEmplacement());
            pst.executeUpdate();
            System.out.println("Event added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding event: " + e.getMessage());
        }
    }

    @Override
    public void create(Event event) throws SQLException {
        // Implement if needed
    }

    @Override
    public int insert(Utilisateur utilisateur) throws SQLException {
        // Implement if needed
        return 0;
    }

    @Override
    public int update(Event event) {
        String query = "UPDATE event SET idOrganisateur=?, nomOrganisateur=?, description=?, dateDebut=?, dateFin=?, emplacement=? WHERE id=?";
        try (Connection connection = DataBase.getInstance().getCnx(); // Get a new connection
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, event.getIdOrganisateur());
            pst.setString(2, event.getNomOrganisateur());
            pst.setString(3, event.getDescription());
            pst.setString(4, event.getDateDebut());
            pst.setString(5, event.getDateFin());
            pst.setString(6, event.getEmplacement());
            pst.setInt(7, event.getId());
            pst.executeUpdate();
            System.out.println("Event updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating event: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int delete(Utilisateur utilisateur) throws SQLException {
        // Implement if needed
        return 0;
    }

    @Override
    public List<Utilisateur> showAll() throws SQLException {
        // Implement if needed
        return List.of();
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM event WHERE id=?";
        try (Connection connection = DataBase.getInstance().getCnx(); // Get a new connection
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Event deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting event: " + e.getMessage());
        }
    }

    @Override
    public List<Event> getAll() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event";
        try (Connection connection = DataBase.getInstance().getCnx(); // Get a new connection
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
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
            System.err.println("Error fetching events: " + e.getMessage());
        }
        return events;
    }

    @Override
    public Event getOneById() throws SQLException {
        // Implement if needed
        return null;
    }

    @Override
    public Utilisateur getOneById(int id) throws SQLException {
        // Implement if needed
        return null;
    }

    @Override
    public Event getById(int id) {
        Event event = null;
        String query = "SELECT * FROM event WHERE id=?";
        try (Connection connection = DataBase.getInstance().getCnx(); // Get a new connection
             PreparedStatement pst = connection.prepareStatement(query)) {
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
            System.err.println("Error fetching event by ID: " + e.getMessage());
        }
        return event;
    }

    public List<Event> getEventsByOrganizer(int idOrganisateur) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE idOrganisateur = ?";
        try (Connection connection = DataBase.getInstance().getCnx(); // Get a new connection
             PreparedStatement pst = connection.prepareStatement(query)) {
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
            System.err.println("Error fetching events by organizer: " + e.getMessage());
        }
        return events;
    }

    public List<Event> getEventsByDate(LocalDate date) {
        List<Event> events = new ArrayList<>();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        System.out.println("Searching for events on date: " + formattedDate);

        String query = "SELECT * FROM event WHERE STR_TO_DATE(?, '%d-%m-%Y') BETWEEN STR_TO_DATE(dateDebut, '%d-%m-%Y') AND STR_TO_DATE(dateFin, '%d-%m-%Y')";
        try (Connection connection = DataBase.getInstance().getCnx(); // Get a new connection
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, formattedDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event(
                        resultSet.getInt("id"),
                        resultSet.getInt("idOrganisateur"),
                        resultSet.getString("nomOrganisateur"),
                        resultSet.getString("description"),
                        resultSet.getString("dateDebut"),
                        resultSet.getString("dateFin"),
                        resultSet.getString("emplacement")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching events by date: " + e.getMessage());
        }
        return events;
    }
}