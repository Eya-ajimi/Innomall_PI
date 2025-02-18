package tn.esprit.entities;

public class Client {
    private int id;
    private int user_id;
    private String firstName;
    private String lastName;
    private int phoneNumber;
    private int points;
    private int rewardsNumber;

    // Constructor
    public Client(int id, int user_id, String firstName, String lastName, int phoneNumber, int points, int rewardsNumber) {
        this.id = id;
        this.user_id = user_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.points = points;
        this.rewardsNumber = rewardsNumber;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return user_id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRewardsNumber() {
        return rewardsNumber;
    }

    public void setRewardsNumber(int rewardsNumber) {
        this.rewardsNumber = rewardsNumber;
    }
}
