package tn.esprit.entites;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MallData {
    private String mallName;
    private List<Store> stores;
    private List<Event> events;
    private Parking parking;

    // Getters and Setters
    public String getMallName() {
        return mallName;
    }

    public void setMallName(String mallName) {
        this.mallName = mallName;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Parking getParking() {
        return parking;
    }

    public void setParking(Parking parking) {
        this.parking = parking;
    }

    // Inner classes for nested JSON data
    public static class Store {
        private String name;
        private int floor;
        private String category;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFloor() {
            return floor;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public static class Event {
        private String name;
        private String date;
        private String location;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public static class Parking {
        private int totalSpaces;
        private int availableSpaces;

        // Getters and Setters
        public int getTotalSpaces() {
            return totalSpaces;
        }

        public void setTotalSpaces(int totalSpaces) {
            this.totalSpaces = totalSpaces;
        }

        public int getAvailableSpaces() {
            return availableSpaces;
        }

        public void setAvailableSpaces(int availableSpaces) {
            this.availableSpaces = availableSpaces;
        }
    }
}
