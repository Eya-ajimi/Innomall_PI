package tn.esprit.services.mariahossservice;
import tn.esprit.entities.Event;
import tn.esprit.utils.DataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService implements CRUD<Event> {
    private Connection connection;

    public EventService() {
        this.connection = DataBase.getInstance().getCnx();
    }

    @Override
    public int insert(Event event) throws SQLException {
        String query = "INSERT INTO event (organisor_id, event_title, description, start_date, end_date, place) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, event.getIdOrganizer());
            pst.setString(2, event.getEventTitle());
            pst.setString(3, event.getDescription());
            pst.setTimestamp(4, event.getStart());
            pst.setTimestamp(5, event.getEnd());
            pst.setString(6, event.getPlace());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return affectedRows;
        }
    }


    @Override
    public int update(Event event) throws SQLException {
        String query = "UPDATE event SET event_title = ?, description = ?, start_date = ?, end_date = ?, place = ? WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, event.getEventTitle());
            pst.setString(2, event.getDescription());
            pst.setTimestamp(3, event.getStart());
            pst.setTimestamp(4, event.getEnd());
            pst.setString(5, event.getPlace());
            pst.setInt(6, event.getId()); // Pass the event ID

            return pst.executeUpdate();
        }
    }

    @Override
    public int delete(Event event) throws SQLException {
        String query = "DELETE FROM event WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, event.getId());
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No event found with ID: " + event.getId());
            }

            return rowsAffected;
        }
    }

    @Override
    public List<Event> showAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getInt("organisor_id"),
                        rs.getString("event_title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getString("place")
                );
                events.add(event);
            }
        }
        return events;
    }



    public Event getEntityById(int id) throws SQLException {
        String query = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Event(
                        rs.getInt("id"),
                        rs.getInt("organisor_id"),
                        rs.getString("event_title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getString("place")
                );
            }
        }
        return null; // Return null if no event is found
    }


    public List<Event> getEventsByShopId(int shopId) throws SQLException {
        String query = "SELECT * FROM event WHERE organisor_id = ?";
        List<Event> eventList = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, shopId);

            // Execute the query
            ResultSet rs = pst.executeQuery();

            // Loop through the result set and add events to the list
            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getInt("organisor_id"), // Correct column name
                        rs.getString("event_title"), // Correct column name
                        rs.getString("description"),
                        rs.getTimestamp("start_date"), // Correct column name
                        rs.getTimestamp("end_date"), // Correct column name
                        rs.getString("place") // Correct column name
                );
                eventList.add(event);
            }
        }

        return eventList;
    }


}
