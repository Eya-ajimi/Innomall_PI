package tn.esprit.tests;

import tn.esprit.entities.PlaceParking;
import tn.esprit.enums.StatutPlace;
import tn.esprit.services.PlaceParkingService;

import java.sql.SQLException;

public class TestPlaceParking {

    public static void main(String[] args) {
        PlaceParkingService placeParkingService = new PlaceParkingService();

        try {
            // Zones A to H, each with 2 spots
            String[] zones = {"A", "B", "C", "D", "E", "F", "G", "H"};

            for (String zone : zones) {
                for (int i = 1; i <= 2; i++) {
                    PlaceParking newPlace = new PlaceParking(0, "Zone " + zone, StatutPlace.free);
                    placeParkingService.insert(newPlace);
                }
            }
            System.out.println("✅ New parking spots added: 2 per zone (A-H).");

            // Display the newly added parking spots
            placeParkingService.showAll().forEach(place ->
                    System.out.println("Place ID: " + place.getId() + ", Zone: " + place.getZone() + ", Status: " + place.getStatut())
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
