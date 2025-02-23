package tn.esprit.tests;

import tn.esprit.entities.PlaceParking;
import tn.esprit.enums.StatutPlace;
import tn.esprit.services.PlaceParkingService;

import java.sql.SQLException;

public class TestPlaceParking {
    public static void main(String[] args) {
        PlaceParkingService service = new PlaceParkingService();

        try {
            // 1. Insert a parking spot with ID 40
            PlaceParking place = new PlaceParking(40, "Zone A", StatutPlace.free, "Level 1"); // Added floor
            service.insert(place);
            System.out.println("Added parking spot ID 40");

            // 2. Update the parking spot
            place.setZone("Zone B"); // Changing zone
            place.setStatut(StatutPlace.taken); // Changing status
            place.setFloor("Level 2"); // Changing floor
            service.update(place);
            System.out.println("Updated parking spot ID 40");

            // 3. Show all parking spots
            System.out.println("All Parking Spots:");
            service.showAll().forEach(System.out::println);

            // 4. Delete the parking spot
            service.delete(place);
            System.out.println("Deleted parking spot ID 40");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}