package tn.esprit.services;

import tn.esprit.entites.EventClient;
import tn.esprit.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventClientService {

    public void addParticipation(EventClient eventClient) {
        String query = "INSERT INTO eventclient (idClient, idEvent, date) VALUES (?, ?, ?)";
        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Log the values being inserted
            System.out.println("Inserting into eventclient: idClient=" + eventClient.getIdClient() +
                    ", idEvent=" + eventClient.getIdEvent() +
                    ", date=" + eventClient.getDate());

            preparedStatement.setInt(1, eventClient.getIdClient());
            preparedStatement.setInt(2, eventClient.getIdEvent());
            preparedStatement.setString(3, eventClient.getDate());
            preparedStatement.executeUpdate();
            System.out.println("Participation added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error: " + e.getMessage()); // Print the error message
        }
    }

    public boolean isParticipating(int idClient, int idEvent) {
        String query = "SELECT * FROM eventclient WHERE idClient = ? AND idEvent = ?";
        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idClient);
            preparedStatement.setInt(2, idEvent); // Corrected from 3 to 2
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}