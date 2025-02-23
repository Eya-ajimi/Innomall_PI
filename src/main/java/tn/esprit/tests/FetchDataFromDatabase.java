package tn.esprit.tests;

import tn.esprit.entities.Utilisateur;
import tn.esprit.entities.PlaceParking;
import tn.esprit.entities.Reservation;
import tn.esprit.enums.Role;
import tn.esprit.enums.StatutPlace;
import tn.esprit.entities.Reservation.StatutReservation; // Import correct de l'énumération
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FetchDataFromDatabase {

    private static Connection connection;

    public static void main(String[] args) {
        connection = DataBase.getInstance().getCnx(); // Récupération de la connexion

        // Récupérer les utilisateurs
        List<Utilisateur> utilisateurs = getAllUtilisateurs();
        System.out.println("Utilisateurs:");
        for (Utilisateur utilisateur : utilisateurs) {
            System.out.println(utilisateur);
        }

        // Récupérer les places de parking
        List<PlaceParking> parkingSpots = getAllParkingSpots();
        System.out.println("Places de parking:");
        for (PlaceParking spot : parkingSpots) {
            System.out.println(spot);
        }

        // Récupérer les réservations
        List<Reservation> reservations = getAllReservations();
        System.out.println("Réservations:");
        for (Reservation reservation : reservations) {
            System.out.println(reservation);
        }
    }

    // Récupérer tous les utilisateurs
    public static List<Utilisateur> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Utilisateur utilisateur = new Utilisateur(
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("email"),
                        resultSet.getString("mot_de_passe"),
                        resultSet.getString("adresse"),
                        resultSet.getString("telephone"),
                        Role.valueOf(resultSet.getString("role")) // Conversion du rôle
                );
                utilisateur.setId(resultSet.getInt("id"));
                utilisateur.setPoints(resultSet.getInt("points"));
                utilisateur.setNombreDeGain(resultSet.getInt("nombre_de_gain"));

                utilisateurs.add(utilisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }

    // Récupérer toutes les places de parking
    public static List<PlaceParking> getAllParkingSpots() {
        List<PlaceParking> parkingSpots = new ArrayList<>();
        String sql = "SELECT * FROM PlaceParking";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PlaceParking spot = new PlaceParking(
                        resultSet.getInt("id"),
                        resultSet.getString("zone"),
                        StatutPlace.valueOf(resultSet.getString("statut")),
                        resultSet.getString("floor") // Ajout du champ floor
                );
                parkingSpots.add(spot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parkingSpots;
    }

    // Récupérer toutes les réservations
    public static List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM Reservation";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Reservation reservation = new Reservation(
                        resultSet.getInt("idClient"),
                        resultSet.getInt("idParking"),
                        resultSet.getTimestamp("dateReservation"),
                        resultSet.getTimestamp("dateExpiration"),
                        Reservation.StatutReservation.valueOf(resultSet.getString("statut")) // Conversion de l'énumération
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }
}