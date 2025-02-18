package tn.esprit.tests;

import tn.esprit.entities.Event;
import tn.esprit.services.EventService;
import tn.esprit.utils.DataBase;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class TestCrudEvent {

    private static EventService eventservice = new EventService();

    public static void main(String[] args) {
        DataBase m1 = DataBase.getInstance();

//      testAddEvent();
//        testGetAllShopEvents();
//        testGetEventById();
//        testUpdateEvent();
//        testDeleteEvent();
    }

    public static void testAddEvent() {
        Event e1 = new Event(1, "meow", "katussa <3 ",
                Timestamp.valueOf("2025-03-05 10:00:00"),
                Timestamp.valueOf("2025-03-05 18:00:00"), "Left Hall");
        try {
            eventservice.insert(e1);
            System.out.println("Event added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding event: " + e.getMessage());
        }
    }

    public static void testGetAllShopEvents() {
        try {
            List<Event> shop_events = eventservice.getEventsByShopId(1);
            if (shop_events.size() > 0) {
                for (Event event : shop_events) {
                    System.out.println(event);
                }
            } else {
                System.out.println("Shop has no events.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching shop events: " + e.getMessage());
        }
    }

    public static void testGetEventById() {
        try {
            Event event = eventservice.getEntityById(1);
            if (event != null) {
                System.out.println(event);
            } else {
                System.out.println("No event found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching event by ID: " + e.getMessage());
        }
    }

    public static void testUpdateEvent() {
        String description_update = "YEEEEEEEEY_Fatales";
        Integer id_organizer_update = null;
        Timestamp start_update = Timestamp.valueOf("2025-03-05 09:00:00");
        Timestamp end_update = Timestamp.valueOf("2025-03-05 17:00:00");
        String place_update = "Main Hall";
        int event_id_update = 1;

        try {
            Event event_update = eventservice.getEntityById(event_id_update);
            if (event_update == null) {
                System.out.println("Event not found!");
            } else {
                System.out.println("Before update: " + event_update);

                // Update only non-null fields
                if (id_organizer_update != null) event_update.setIdOrganizerr(id_organizer_update);
                if (start_update != null) event_update.setStart(start_update);
                if (end_update != null) event_update.setEnd(end_update);
                if (place_update != null && !place_update.isEmpty()) event_update.setPlace(place_update);
                if (description_update != null && !description_update.isEmpty()) event_update.setDescription(description_update);

                int rowsAffected = eventservice.update(event_update);
                if (rowsAffected > 0) {
                    System.out.println("Event updated successfully.");
                } else {
                    System.out.println("No changes were made.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating event: " + e.getMessage());
        }
    }

    public static void testDeleteEvent() {
        int event_id_delete = 2;
        try {
            Event event_delete = eventservice.getEntityById(event_id_delete);
            if (event_delete == null) {
                System.out.println("Event not found!");
            } else {
                System.out.println("Event to be deleted: " + event_delete);
                eventservice.delete(event_delete);
                System.out.println("Event deleted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting event: " + e.getMessage());
        }
    }
}
